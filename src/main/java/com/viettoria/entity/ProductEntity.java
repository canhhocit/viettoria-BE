package com.viettoria.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, unique = true, length = 64)
    private String code; // e.g. "VT-HN-001"

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "sub_name", length = 255)
    private String subName;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "collection_id", length = 64)
    private String collectionId;

    @Column(length = 128)
    private String epoch; // Thời Lý, Triều Nguyễn, Cận đại...

    @Column(length = 128)
    private String location; // Hà Nội, Huế, TP. Hồ Chí Minh...

    @Lob
    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    // Model Specifications
    @Column(name = "scale_ratio", length = 32)
    private String scaleRatio = "1:120";

    @Column(name = "height_cm")
    private double heightCm = 12.0;

    @Column(name = "width_cm")
    private double widthCm = 10.0;

    @Column(name = "weight_grams")
    private double weightGrams = 480.0;

    @Column(length = 128)
    private String material = "BioPETG Khoáng Đá Tự Nhiên";

    @Column(name = "parts_count")
    private int partsCount = 24;

    @Column(length = 255)
    private String finishing = "Sơn then & thếp vàng thủ công";

    // Imagery
    @Column(name = "hero_image", length = 500)
    private String heroImage;

    @Lob
    @Column(name = "gallery_images_json", columnDefinition = "TEXT")
    private String galleryImagesJson = "[]";

    @Column(name = "model_3d_url", length = 500)
    private String model3dUrl;

    // Audio Guide fields
    @Column(name = "audio_title", length = 255)
    private String audioTitle;

    @Column(name = "audio_duration", length = 32)
    private String audioDuration;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "audio_narrator", length = 128)
    private String audioNarrator;

    @Lob
    @Column(name = "audio_transcript", columnDefinition = "TEXT")
    private String audioTranscript;

    @Column(name = "display_order")
    private int displayOrder = 1;

    @Column(length = 32)
    private String status = "published";

    @Column(name = "is_hero_release")
    private boolean isHeroRelease = false;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public ProductEntity() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubName() { return subName; }
    public void setSubName(String subName) { this.subName = subName; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCollectionId() { return collectionId; }
    public void setCollectionId(String collectionId) { this.collectionId = collectionId; }

    public String getEpoch() { return epoch; }
    public void setEpoch(String epoch) { this.epoch = epoch; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getScaleRatio() { return scaleRatio; }
    public void setScaleRatio(String scaleRatio) { this.scaleRatio = scaleRatio; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public double getWidthCm() { return widthCm; }
    public void setWidthCm(double widthCm) { this.widthCm = widthCm; }

    public double getWeightGrams() { return weightGrams; }
    public void setWeightGrams(double weightGrams) { this.weightGrams = weightGrams; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public int getPartsCount() { return partsCount; }
    public void setPartsCount(int partsCount) { this.partsCount = partsCount; }

    public String getFinishing() { return finishing; }
    public void setFinishing(String finishing) { this.finishing = finishing; }

    public String getHeroImage() { return heroImage; }
    public void setHeroImage(String heroImage) { this.heroImage = heroImage; }

    public String getGalleryImagesJson() { return galleryImagesJson; }
    public void setGalleryImagesJson(String galleryImagesJson) { this.galleryImagesJson = galleryImagesJson; }

    public String getModel3dUrl() { return model3dUrl; }
    public void setModel3dUrl(String model3dUrl) { this.model3dUrl = model3dUrl; }

    public String getAudioTitle() { return audioTitle; }
    public void setAudioTitle(String audioTitle) { this.audioTitle = audioTitle; }

    public String getAudioDuration() { return audioDuration; }
    public void setAudioDuration(String audioDuration) { this.audioDuration = audioDuration; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getAudioNarrator() { return audioNarrator; }
    public void setAudioNarrator(String audioNarrator) { this.audioNarrator = audioNarrator; }

    public String getAudioTranscript() { return audioTranscript; }
    public void setAudioTranscript(String audioTranscript) { this.audioTranscript = audioTranscript; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isHeroRelease() { return isHeroRelease; }
    public void setHeroRelease(boolean heroRelease) { isHeroRelease = heroRelease; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
