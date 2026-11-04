package com.viettoria.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "media_items")
public class MediaItemEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type", length = 64)
    private String fileType;

    @Column(name = "file_size")
    private long fileSize;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "public_id", length = 255)
    private String publicId;

    @Column(name = "resource_type", length = 16)
    private String resourceType;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "uploaded_at", length = 32)
    private String uploadedAt = "2026-03-30";

    public MediaItemEntity() {}

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
