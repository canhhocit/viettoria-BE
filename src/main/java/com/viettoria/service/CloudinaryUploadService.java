package com.viettoria.service;

import com.viettoria.dto.CloudinaryUploadSignatureDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CloudinaryUploadService {

    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public CloudinaryUploadService(
            @Value("${app.cloudinary.cloud-name:}") String cloudName,
            @Value("${app.cloudinary.api-key:}") String apiKey,
            @Value("${app.cloudinary.api-secret:}") String apiSecret) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public CloudinaryUploadSignatureDto createUploadSignature(String resourceType, String fileExtension) {
        ensureConfigured();

        if (!"image".equals(resourceType) && !"raw".equals(resourceType) && !"video".equals(resourceType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại tệp chỉ hỗ trợ ảnh, mô hình GLB hoặc âm thanh.");
        }
        if ("raw".equals(resourceType) && !"glb".equalsIgnoreCase(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hiện chỉ hỗ trợ mô hình 3D định dạng GLB.");
        }
        if ("video".equals(resourceType) && !isSupportedAudioExtension(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Âm thanh chỉ hỗ trợ MP3, WAV, M4A, OGG hoặc FLAC.");
        }

        String folder = switch (resourceType) {
            case "image" -> "viettoria/images";
            case "raw" -> "viettoria/3d";
            default -> "viettoria/audio";
        };
        String publicId = UUID.randomUUID().toString();
        if ("raw".equals(resourceType)) {
            // Cloudinary raw asset public IDs include the file extension.
            publicId += ".glb";
        }
        long timestamp = Instant.now().getEpochSecond();
        Map<String, String> signedParameters = new TreeMap<>();
        signedParameters.put("folder", folder);
        signedParameters.put("overwrite", "false");
        signedParameters.put("public_id", publicId);
        signedParameters.put("timestamp", Long.toString(timestamp));

        String stringToSign = signedParameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest((stringToSign + apiSecret).getBytes(StandardCharsets.UTF_8));
            return new CloudinaryUploadSignatureDto(
                    cloudName, apiKey, timestamp, HexFormat.of().formatHex(hash),
                    folder, publicId, resourceType, false);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Không thể tạo chữ ký tải lên Cloudinary.", exception);
        }
    }

    public void validateUploadedMedia(String url, String resourceType, String publicId) {
        ensureConfigured();
        if (!"image".equals(resourceType) && !"raw".equals(resourceType) && !"video".equals(resourceType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại tài nguyên Cloudinary không hợp lệ.");
        }
        String expectedPrefix = "https://res.cloudinary.com/" + cloudName + "/" + resourceType + "/upload/";
        if (url == null || !url.startsWith(expectedPrefix) || publicId == null || publicId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp phải được tải lên Cloudinary của VIETTORIA trước khi lưu.");
        }
    }

    public void deleteUploadedMedia(String publicId, String resourceType) {
        ensureConfigured();
        if (publicId == null || publicId.isBlank()
                || (!"image".equals(resourceType) && !"raw".equals(resourceType) && !"video".equals(resourceType))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thông tin tệp Cloudinary không hợp lệ.");
        }

        long timestamp = Instant.now().getEpochSecond();
        String stringToSign = "public_id=" + publicId + "&timestamp=" + timestamp;
        String signature = sign(stringToSign);
        String body = formValue("public_id", publicId)
                + "&" + formValue("timestamp", Long.toString(timestamp))
                + "&" + formValue("api_key", apiKey)
                + "&" + formValue("signature", signature);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.cloudinary.com/v1_1/" + cloudName + "/" + resourceType + "/destroy"))
                .timeout(Duration.ofSeconds(25))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body() == null ? "" : response.body();
            boolean removedOrMissing = responseBody.matches("(?s).*\\\"result\\\"\\s*:\\s*\\\"(ok|not found)\\\".*");
            if (response.statusCode() < 200 || response.statusCode() >= 300 || !removedOrMissing) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cloudinary không thể xóa tệp này.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Yêu cầu xóa Cloudinary bị gián đoạn.", exception);
        } catch (java.io.IOException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Không kết nối được Cloudinary để xóa tệp.", exception);
        }
    }

    private String sign(String stringToSign) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest((stringToSign + apiSecret).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Không thể ký yêu cầu xóa Cloudinary.", exception);
        }
    }

    private String formValue(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private void ensureConfigured() {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Cloudinary chưa được cấu hình. Hãy thiết lập CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY và CLOUDINARY_API_SECRET ở backend.");
        }
    }

    private boolean isSupportedAudioExtension(String fileExtension) {
        if (fileExtension == null) return false;
        return switch (fileExtension.toLowerCase(Locale.ROOT)) {
            case "mp3", "wav", "m4a", "ogg", "flac" -> true;
            default -> false;
        };
    }
}
