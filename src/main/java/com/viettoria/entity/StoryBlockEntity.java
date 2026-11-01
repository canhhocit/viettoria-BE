package com.viettoria.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "story_blocks")
public class StoryBlockEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(nullable = false, length = 32)
    private String type; // 'text' | 'image' | 'text_image' | 'timeline' | 'highlight' | 'assembly'

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Column(name = "media_caption", length = 255)
    private String mediaCaption;

    @Lob
    @Column(name = "timeline_items_json", columnDefinition = "TEXT")
    private String timelineItemsJson;

    @Lob
    @Column(name = "assembly_steps_json", columnDefinition = "TEXT")
    private String assemblyStepsJson;

    @Column(name = "highlight_badge", length = 128)
    private String highlightBadge;

    @Column(name = "display_order")
    private int displayOrder = 1;

    @Column(length = 32)
    private String status = "published";

    public StoryBlockEntity() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getMediaCaption() { return mediaCaption; }
    public void setMediaCaption(String mediaCaption) { this.mediaCaption = mediaCaption; }

    public String getTimelineItemsJson() { return timelineItemsJson; }
    public void setTimelineItemsJson(String timelineItemsJson) { this.timelineItemsJson = timelineItemsJson; }

    public String getAssemblyStepsJson() { return assemblyStepsJson; }
    public void setAssemblyStepsJson(String assemblyStepsJson) { this.assemblyStepsJson = assemblyStepsJson; }

    public String getHighlightBadge() { return highlightBadge; }
    public void setHighlightBadge(String highlightBadge) { this.highlightBadge = highlightBadge; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
