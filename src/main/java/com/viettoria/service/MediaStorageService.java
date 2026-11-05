package com.viettoria.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
public class MediaStorageService {

    private final Path uploadDirectory;

    public MediaStorageService(@Value("${app.upload.dir:uploads}") String uploadDirectory) {
        this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    }

    public StoredMedia store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp tải lên đang trống.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không xác định được tên tệp.");
        }
        originalName = originalName.replace('\\', '/');
        originalName = originalName.substring(originalName.lastIndexOf('/') + 1);
        if (originalName.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên tệp vượt quá 255 ký tự.");
        }

        int extensionSeparator = originalName.lastIndexOf('.');
        if (extensionSeparator < 0 || extensionSeparator == originalName.length() - 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp phải có phần mở rộng được hỗ trợ.");
        }
        String extension = originalName.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT);
        String fileType = switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "avif" -> "image/avif";
            case "gif" -> "image/gif";
            case "glb" -> "model/gltf-binary";
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "m4a" -> "audio/mp4";
            case "ogg" -> "audio/ogg";
            case "flac" -> "audio/flac";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Định dạng chỉ hỗ trợ JPG, PNG, WebP, AVIF, GIF, GLB, MP3, WAV, M4A, OGG và FLAC.");
        };
        validateFileSignature(file, extension);

        String storedName = UUID.randomUUID() + "." + extension;
        try {
            Files.createDirectories(uploadDirectory);
            Path destination = uploadDirectory.resolve(storedName).normalize();
            if (!destination.getParent().equals(uploadDirectory)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đường dẫn tệp không hợp lệ.");
            }
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination);
            }
            String resourceType = switch (extension) {
                case "glb" -> "raw";
                case "mp3", "wav", "m4a", "ogg", "flac" -> "video";
                default -> "image";
            };
            return new StoredMedia(originalName, fileType, "/uploads/" + storedName, resourceType, file.getSize());
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lưu tệp vào backend.", exception);
        }
    }

    public Path getUploadDirectory() {
        return uploadDirectory;
    }

    public void deleteStoredMedia(String mediaUrl) {
        if (mediaUrl == null || !mediaUrl.startsWith("/uploads/")) {
            return;
        }
        String fileName = mediaUrl.substring("/uploads/".length());
        if (fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đường dẫn tệp backend không hợp lệ.");
        }
        Path file = uploadDirectory.resolve(fileName).normalize();
        if (!file.startsWith(uploadDirectory)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đường dẫn tệp backend không hợp lệ.");
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể xóa tệp khỏi backend.", exception);
        }
    }

    private void validateFileSignature(MultipartFile file, String extension) {
        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(16);
            boolean valid = switch (extension) {
                case "jpg", "jpeg" -> header.length >= 3 && (header[0] & 0xff) == 0xff
                        && (header[1] & 0xff) == 0xd8 && (header[2] & 0xff) == 0xff;
                case "png" -> startsWith(header, new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a});
                case "gif" -> startsWithAscii(header, "GIF87a") || startsWithAscii(header, "GIF89a");
                case "webp" -> header.length >= 12 && startsWithAscii(header, "RIFF") && asciiAt(header, 8, "WEBP");
                case "avif" -> header.length >= 12 && asciiAt(header, 4, "ftyp")
                        && (containsAsciiAtOrAfter(header, 8, "avif") || containsAsciiAtOrAfter(header, 8, "avis"));
                case "glb" -> startsWithAscii(header, "glTF");
                case "mp3" -> startsWithAscii(header, "ID3") || (header.length >= 2
                        && (header[0] & 0xff) == 0xff && (header[1] & 0xe0) == 0xe0);
                case "wav" -> header.length >= 12 && startsWithAscii(header, "RIFF") && asciiAt(header, 8, "WAVE");
                case "m4a" -> header.length >= 12 && asciiAt(header, 4, "ftyp");
                case "ogg" -> startsWithAscii(header, "OggS");
                case "flac" -> startsWithAscii(header, "fLaC");
                default -> false;
            };
            if (!valid) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nội dung tệp không khớp với định dạng đã chọn.");
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể đọc tệp tải lên.", exception);
        }
    }

    private boolean startsWith(byte[] value, byte[] prefix) {
        if (value.length < prefix.length) return false;
        for (int index = 0; index < prefix.length; index++) {
            if (value[index] != prefix[index]) return false;
        }
        return true;
    }

    private boolean startsWithAscii(byte[] value, String prefix) {
        return asciiAt(value, 0, prefix);
    }

    private boolean asciiAt(byte[] value, int offset, String expected) {
        if (value.length < offset + expected.length()) return false;
        for (int index = 0; index < expected.length(); index++) {
            if (value[offset + index] != (byte) expected.charAt(index)) return false;
        }
        return true;
    }

    private boolean containsAsciiAtOrAfter(byte[] value, int offset, String expected) {
        for (int index = offset; index <= value.length - expected.length(); index++) {
            if (asciiAt(value, index, expected)) return true;
        }
        return false;
    }

    public record StoredMedia(String originalName, String fileType, String url, String resourceType, long fileSize) {}
}
