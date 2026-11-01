package com.viettoria.repository;

import com.viettoria.entity.StoryBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryBlockRepository extends JpaRepository<StoryBlockEntity, String> {
    List<StoryBlockEntity> findByProductIdOrderByDisplayOrderAsc(String productId);
    List<StoryBlockEntity> findByProductIdAndStatusOrderByDisplayOrderAsc(String productId, String status);
    void deleteByProductId(String productId);
}
