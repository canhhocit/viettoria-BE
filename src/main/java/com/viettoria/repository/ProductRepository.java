package com.viettoria.repository;

import com.viettoria.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findBySlug(String slug);
    Optional<ProductEntity> findByCodeIgnoreCase(String code);

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.slug) = LOWER(:slugOrCode) OR LOWER(p.code) = LOWER(:slugOrCode)")
    Optional<ProductEntity> findBySlugOrCode(@Param("slugOrCode") String slugOrCode);

    List<ProductEntity> findByStatusOrderByDisplayOrderAsc(String status);
    List<ProductEntity> findAllByOrderByDisplayOrderAsc();

    List<ProductEntity> findByCollectionIdAndStatusOrderByDisplayOrderAsc(String collectionId, String status);
    List<ProductEntity> findByCollectionId(String collectionId);

    long countByStatus(String status);
}
