package com.viettoria.dto;

public class MediaItemDto {
    private String id;
    private String name;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String url;
    private String publicId;
    private String resourceType;
    private String altText;
    private String uploadedAt;

    public MediaItemDto() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}
