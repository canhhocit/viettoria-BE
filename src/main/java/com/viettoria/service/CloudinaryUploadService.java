package com.viettoria.service;

import com.viettoria.dto.CloudinaryUploadSignatureDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
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

        if (!"image".equals(resourceType) && !"raw".equals(resourceType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ tải ảnh hoặc mô hình 3D GLB.");
        }
        if ("raw".equals(resourceType) && !"glb".equalsIgnoreCase(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hiện chỉ hỗ trợ mô hình 3D định dạng GLB.");
        }

        String folder = "image".equals(resourceType) ? "viettoria/images" : "viettoria/3d";
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
        if (!"image".equals(resourceType) && !"raw".equals(resourceType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại tài nguyên Cloudinary không hợp lệ.");
        }
        String expectedPrefix = "https://res.cloudinary.com/" + cloudName + "/" + resourceType + "/upload/";
        if (url == null || !url.startsWith(expectedPrefix) || publicId == null || publicId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp phải được tải lên Cloudinary của VIETTORIA trước khi lưu.");
        }
    }

    private void ensureConfigured() {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Cloudinary chưa được cấu hình. Hãy thiết lập CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY và CLOUDINARY_API_SECRET ở backend.");
        }
    }
}
