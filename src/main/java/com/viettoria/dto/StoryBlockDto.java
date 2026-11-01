package com.viettoria.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryBlockDto {
    private String id;
    private String productId;
    private String type; // 'text' | 'image' | 'text_image' | 'timeline' | 'highlight' | 'assembly'
    private String title;
    private String subtitle;
    private String content;
    private String mediaUrl;
    private String mediaCaption;
    private Object timelineItems;
    private Object assemblySteps;
    private String highlightBadge;
    private int displayOrder;
    private String status;

    public StoryBlockDto() {}

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

    public Object getTimelineItems() { return timelineItems; }
    public void setTimelineItems(Object timelineItems) { this.timelineItems = timelineItems; }

    public Object getAssemblySteps() { return assemblySteps; }
    public void setAssemblySteps(Object assemblySteps) { this.assemblySteps = assemblySteps; }

    public String getHighlightBadge() { return highlightBadge; }
    public void setHighlightBadge(String highlightBadge) { this.highlightBadge = highlightBadge; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
