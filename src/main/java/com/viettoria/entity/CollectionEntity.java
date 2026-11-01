package com.viettoria.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "collections")
public class CollectionEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "hero_image", length = 500)
    private String heroImage;

    @Column(name = "display_order")
    private int displayOrder = 1;

    @Column(length = 32)
    private String status = "published";

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public CollectionEntity() {}

    public CollectionEntity(String id, String code, String name, String slug, String description, String heroImage, int displayOrder, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.heroImage = heroImage;
        this.displayOrder = displayOrder;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHeroImage() { return heroImage; }
    public void setHeroImage(String heroImage) { this.heroImage = heroImage; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
