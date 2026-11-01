package com.viettoria.repository;

import com.viettoria.entity.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionEntity, String> {
    Optional<CollectionEntity> findBySlug(String slug);
    List<CollectionEntity> findByStatusOrderByDisplayOrderAsc(String status);
    List<CollectionEntity> findAllByOrderByDisplayOrderAsc();
}
