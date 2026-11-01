package com.viettoria.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "blog_posts")
public class BlogPostEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @Column(length = 128)
    private String author = "Ban Cố Vấn Văn Hóa";

    @Column(length = 128)
    private String category = "Khảo Cứu Kiến Trúc";

    @Column(name = "read_time", length = 32)
    private String readTime = "5 phút đọc";

    @Column(name = "published_at", length = 32)
    private String publishedAt = "2026-03-30";

    @Column(length = 32)
    private String status = "published";

    public BlogPostEntity() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
