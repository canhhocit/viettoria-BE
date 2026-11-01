package com.viettoria.repository;

import com.viettoria.entity.BlogPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPostEntity, String> {
    Optional<BlogPostEntity> findBySlug(String slug);
    List<BlogPostEntity> findByStatusOrderByPublishedAtDesc(String status);
    List<BlogPostEntity> findAllByOrderByPublishedAtDesc();
}
