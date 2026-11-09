package com.viettoria.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_translations", uniqueConstraints = {
    @UniqueConstraint(name = "uk_translation_entity_lang", columnNames = {"entity_type", "entity_id", "lang_code"})
}, indexes = {
    @Index(name = "idx_translation_lookup", columnList = "entity_type, entity_id, lang_code"),
    @Index(name = "idx_translation_lang", columnList = "lang_code")
})
public class ContentTranslationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 32)
    private String entityType; // "blog", "collection", "product", "story_block"

    @Column(name = "entity_id", nullable = false, length = 64)
    private String entityId;

    @Column(name = "lang_code", nullable = false, length = 10)
    private String langCode; // "en", "fr", "zh", "ko", "ja"

    @Lob
    @Column(name = "fields_json", columnDefinition = "TEXT", nullable = false)
    private String fieldsJson; // JSON payload with localized key-values

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ContentTranslationEntity() {}

    public ContentTranslationEntity(String entityType, String entityId, String langCode, String fieldsJson) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.langCode = langCode;
        this.fieldsJson = fieldsJson;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getFieldsJson() {
        return fieldsJson;
    }

    public void setFieldsJson(String fieldsJson) {
        this.fieldsJson = fieldsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

