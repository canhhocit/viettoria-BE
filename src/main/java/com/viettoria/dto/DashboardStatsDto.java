package com.viettoria.dto;

public class DashboardStatsDto {
    private long totalProducts;
    private long publishedProducts;
    private long totalCollections;
    private long totalBlogs;
    private long totalStoryBlocks;
    private long totalMedia;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalProducts, long publishedProducts, long totalCollections, long totalBlogs, long totalStoryBlocks, long totalMedia) {
        this.totalProducts = totalProducts;
        this.publishedProducts = publishedProducts;
        this.totalCollections = totalCollections;
        this.totalBlogs = totalBlogs;
        this.totalStoryBlocks = totalStoryBlocks;
        this.totalMedia = totalMedia;
    }

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getPublishedProducts() { return publishedProducts; }
    public void setPublishedProducts(long publishedProducts) { this.publishedProducts = publishedProducts; }

    public long getTotalCollections() { return totalCollections; }
    public void setTotalCollections(long totalCollections) { this.totalCollections = totalCollections; }

    public long getTotalBlogs() { return totalBlogs; }
    public void setTotalBlogs(long totalBlogs) { this.totalBlogs = totalBlogs; }

    public long getTotalStoryBlocks() { return totalStoryBlocks; }
    public void setTotalStoryBlocks(long totalStoryBlocks) { this.totalStoryBlocks = totalStoryBlocks; }

    public long getTotalMedia() { return totalMedia; }
    public void setTotalMedia(long totalMedia) { this.totalMedia = totalMedia; }
}
