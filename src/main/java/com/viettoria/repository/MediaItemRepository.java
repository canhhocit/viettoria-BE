package com.viettoria.repository;

import com.viettoria.entity.MediaItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaItemRepository extends JpaRepository<MediaItemEntity, String> {
    List<MediaItemEntity> findAllByOrderByUploadedAtDesc();
}
