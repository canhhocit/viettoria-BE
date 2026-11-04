package com.viettoria.dto;

public record CloudinaryUploadSignatureDto(
        String cloudName,
        String apiKey,
        long timestamp,
        String signature,
        String folder,
        String publicId,
        String resourceType,
        boolean overwrite) {
}
