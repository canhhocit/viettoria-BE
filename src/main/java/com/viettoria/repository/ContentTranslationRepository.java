package com.viettoria.repository;

import com.viettoria.entity.ContentTranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentTranslationRepository extends JpaRepository<ContentTranslationEntity, Long> {

    Optional<ContentTranslationEntity> findByEntityTypeAndEntityIdAndLangCode(String entityType, String entityId, String langCode);

    List<ContentTranslationEntity> findByEntityTypeAndEntityIdInAndLangCode(String entityType, Collection<String> entityIds, String langCode);

    List<ContentTranslationEntity> findByEntityTypeAndEntityId(String entityType, String entityId);

    long countByEntityTypeAndEntityId(String entityType, String entityId);

    void deleteByEntityTypeAndEntityId(String entityType, String entityId);
}
