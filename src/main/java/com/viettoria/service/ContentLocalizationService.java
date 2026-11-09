package com.viettoria.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettoria.dto.BlogPostDto;
import com.viettoria.dto.CollectionDto;
import com.viettoria.dto.ProductDto;
import com.viettoria.dto.StoryBlockDto;
import com.viettoria.entity.ContentTranslationEntity;
import com.viettoria.repository.ContentTranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentLocalizationService {

    private static final Logger log = LoggerFactory.getLogger(ContentLocalizationService.class);

    public static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("en", "fr", "zh", "ko", "ja");

    private final ContentTranslationRepository translationRepo;
    private final GeminiTranslationService geminiService;
    private final ObjectMapper objectMapper;

    public ContentLocalizationService(
            ContentTranslationRepository translationRepo,
            GeminiTranslationService geminiService,
            ObjectMapper objectMapper) {
        this.translationRepo = translationRepo;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    /**
     * Resolves the effective language code from either a query parameter or an Accept-Language header.
     */
    public String resolveLanguage(String langParam, String acceptLanguage) {
        if (langParam != null && !langParam.trim().isEmpty()) {
            return normalizeLangCode(langParam);
        }
        if (acceptLanguage != null && !acceptLanguage.trim().isEmpty()) {
            return normalizeLangCode(acceptLanguage);
        }
        return "vi";
    }

    private String normalizeLangCode(String raw) {
        if (raw == null) return "vi";
        String first = raw.split("[,;\\s]")[0].trim().toLowerCase();
        if (first.startsWith("en")) return "en";
        if (first.startsWith("fr")) return "fr";
        if (first.startsWith("zh")) return "zh";
        if (first.startsWith("ko")) return "ko";
        if (first.startsWith("ja")) return "ja";
        return "vi";
    }

    // -------------------------------------------------------------
    // BLOG POST LOCALIZATION
    // -------------------------------------------------------------

    public BlogPostDto localizeBlog(BlogPostDto dto, String lang) {
        if (dto == null || isVietnamese(lang)) return dto;

        Optional<ContentTranslationEntity> cached = translationRepo.findByEntityTypeAndEntityIdAndLangCode("blog", dto.getId(), lang);
        if (cached.isPresent()) {
            return applyBlogTranslation(dto, cached.get().getFieldsJson());
        }

        // On-demand AI translation
        if (geminiService.isConfigured()) {
            Map<String, String> source = new HashMap<>();
            if (dto.getTitle() != null) source.put("title", dto.getTitle());
            if (dto.getExcerpt() != null) source.put("excerpt", dto.getExcerpt());
            if (dto.getContent() != null) source.put("content", dto.getContent());
            if (dto.getCategory() != null) source.put("category", dto.getCategory());
            if (dto.getReadTime() != null) source.put("readTime", dto.getReadTime());

            Map<String, String> translated = geminiService.translateFields(source, lang, "Vietnamese Architectural & Cultural Heritage Essay");
            if (translated != null && !translated.isEmpty()) {
                saveTranslation("blog", dto.getId(), lang, translated);
                return applyBlogTranslation(dto, translated);
            }
        }

        return dto;
    }

    public List<BlogPostDto> localizeBlogs(List<BlogPostDto> list, String lang) {
        if (list == null || list.isEmpty() || isVietnamese(lang)) return list;

        List<String> ids = list.stream().map(BlogPostDto::getId).collect(Collectors.toList());
        List<ContentTranslationEntity> cachedList = translationRepo.findByEntityTypeAndEntityIdInAndLangCode("blog", ids, lang);
        Map<String, String> cachedMap = cachedList.stream()
                .collect(Collectors.toMap(ContentTranslationEntity::getEntityId, ContentTranslationEntity::getFieldsJson, (a, b) -> a));

        return list.stream().map(item -> {
            String fieldsJson = cachedMap.get(item.getId());
            if (fieldsJson != null) {
                return applyBlogTranslation(item, fieldsJson);
            }
            return item;
        }).collect(Collectors.toList());
    }

    private BlogPostDto applyBlogTranslation(BlogPostDto dto, String fieldsJson) {
        try {
            Map<String, String> map = objectMapper.readValue(fieldsJson, new TypeReference<Map<String, String>>() {});
            return applyBlogTranslation(dto, map);
        } catch (Exception e) {
            log.warn("Failed to parse cached translation for blog {}: {}", dto.getId(), e.getMessage());
            return dto;
        }
    }

    private BlogPostDto applyBlogTranslation(BlogPostDto dto, Map<String, String> map) {
        if (map == null) return dto;
        BlogPostDto copy = new BlogPostDto();
        copy.setId(dto.getId());
        copy.setSlug(dto.getSlug());
        copy.setCoverImage(dto.getCoverImage());
        copy.setAuthor(dto.getAuthor());
        copy.setPublishedAt(dto.getPublishedAt());
        copy.setStatus(dto.getStatus());

        copy.setTitle(map.getOrDefault("title", dto.getTitle()));
        copy.setExcerpt(map.getOrDefault("excerpt", dto.getExcerpt()));
        copy.setContent(map.getOrDefault("content", dto.getContent()));
        copy.setCategory(map.getOrDefault("category", dto.getCategory()));
        copy.setReadTime(map.getOrDefault("readTime", dto.getReadTime()));
        return copy;
    }

    // -------------------------------------------------------------
    // COLLECTION LOCALIZATION
    // -------------------------------------------------------------

    public CollectionDto localizeCollection(CollectionDto dto, String lang) {
        if (dto == null || isVietnamese(lang)) return dto;

        Optional<ContentTranslationEntity> cached = translationRepo.findByEntityTypeAndEntityIdAndLangCode("collection", dto.getId(), lang);
        if (cached.isPresent()) {
            return applyCollectionTranslation(dto, cached.get().getFieldsJson());
        }

        if (geminiService.isConfigured()) {
            Map<String, String> source = new HashMap<>();
            if (dto.getName() != null) source.put("name", dto.getName());
            if (dto.getDescription() != null) source.put("description", dto.getDescription());

            Map<String, String> translated = geminiService.translateFields(source, lang, "Heritage Monument Collection / Exhibition");
            if (translated != null && !translated.isEmpty()) {
                saveTranslation("collection", dto.getId(), lang, translated);
                return applyCollectionTranslation(dto, translated);
            }
        }

        return dto;
    }

    public List<CollectionDto> localizeCollections(List<CollectionDto> list, String lang) {
        if (list == null || list.isEmpty() || isVietnamese(lang)) return list;

        List<String> ids = list.stream().map(CollectionDto::getId).collect(Collectors.toList());
        List<ContentTranslationEntity> cachedList = translationRepo.findByEntityTypeAndEntityIdInAndLangCode("collection", ids, lang);
        Map<String, String> cachedMap = cachedList.stream()
                .collect(Collectors.toMap(ContentTranslationEntity::getEntityId, ContentTranslationEntity::getFieldsJson, (a, b) -> a));

        return list.stream().map(item -> {
            String fieldsJson = cachedMap.get(item.getId());
            if (fieldsJson != null) {
                return applyCollectionTranslation(item, fieldsJson);
            }
            return item;
        }).collect(Collectors.toList());
    }

    private CollectionDto applyCollectionTranslation(CollectionDto dto, String fieldsJson) {
        try {
            Map<String, String> map = objectMapper.readValue(fieldsJson, new TypeReference<Map<String, String>>() {});
            return applyCollectionTranslation(dto, map);
        } catch (Exception e) {
            return dto;
        }
    }

    private CollectionDto applyCollectionTranslation(CollectionDto dto, Map<String, String> map) {
        if (map == null) return dto;
        CollectionDto copy = new CollectionDto();
        copy.setId(dto.getId());
        copy.setCode(dto.getCode());
        copy.setSlug(dto.getSlug());
        copy.setHeroImage(dto.getHeroImage());
        copy.setDisplayOrder(dto.getDisplayOrder());
        copy.setStatus(dto.getStatus());
        copy.setName(map.getOrDefault("name", dto.getName()));
        copy.setDescription(map.getOrDefault("description", dto.getDescription()));
        copy.setProductsCount(dto.getProductsCount());
        return copy;
    }

    // -------------------------------------------------------------
    // PRODUCT LOCALIZATION
    // -------------------------------------------------------------

    public ProductDto localizeProduct(ProductDto dto, String lang) {
        if (dto == null || isVietnamese(lang)) return dto;

        ProductDto copy = dto;

        Optional<ContentTranslationEntity> cached = translationRepo.findByEntityTypeAndEntityIdAndLangCode("product", dto.getId(), lang);
        if (cached.isPresent()) {
            copy = applyProductTranslation(dto, cached.get().getFieldsJson());
        } else if (geminiService.isConfigured()) {
            Map<String, String> source = new HashMap<>();
            if (dto.getName() != null) source.put("name", dto.getName());
            if (dto.getSubName() != null) source.put("subName", dto.getSubName());
            if (dto.getShortDescription() != null) source.put("shortDescription", dto.getShortDescription());
            if (dto.getEpoch() != null) source.put("epoch", dto.getEpoch());
            if (dto.getLocation() != null) source.put("location", dto.getLocation());

            Map<String, String> translated = geminiService.translateFields(source, lang, "Vietnamese Architectural Monument 3D Collectible");
            if (translated != null && !translated.isEmpty()) {
                saveTranslation("product", dto.getId(), lang, translated);
                copy = applyProductTranslation(dto, translated);
            }
        }

        // Localize nested collection if present
        if (copy.getCollection() != null) {
            copy.setCollection(localizeCollection(copy.getCollection(), lang));
        }

        // Localize story blocks if present
        if (copy.getStoryBlocks() != null && !copy.getStoryBlocks().isEmpty()) {
            copy.setStoryBlocks(localizeStoryBlocks(copy.getStoryBlocks(), lang));
        }

        return copy;
    }

    public List<ProductDto> localizeProducts(List<ProductDto> list, String lang) {
        if (list == null || list.isEmpty() || isVietnamese(lang)) return list;

        List<String> ids = list.stream().map(ProductDto::getId).collect(Collectors.toList());
        List<ContentTranslationEntity> cachedList = translationRepo.findByEntityTypeAndEntityIdInAndLangCode("product", ids, lang);
        Map<String, String> cachedMap = cachedList.stream()
                .collect(Collectors.toMap(ContentTranslationEntity::getEntityId, ContentTranslationEntity::getFieldsJson, (a, b) -> a));

        return list.stream().map(item -> {
            String fieldsJson = cachedMap.get(item.getId());
            ProductDto result = (fieldsJson != null) ? applyProductTranslation(item, fieldsJson) : item;
            if (result.getCollection() != null) {
                result.setCollection(localizeCollection(result.getCollection(), lang));
            }
            return result;
        }).collect(Collectors.toList());
    }

    private ProductDto applyProductTranslation(ProductDto dto, String fieldsJson) {
        try {
            Map<String, String> map = objectMapper.readValue(fieldsJson, new TypeReference<Map<String, String>>() {});
            return applyProductTranslation(dto, map);
        } catch (Exception e) {
            return dto;
        }
    }

    private ProductDto applyProductTranslation(ProductDto dto, Map<String, String> map) {
        if (map == null) return dto;
        ProductDto copy = new ProductDto();
        copy.setId(dto.getId());
        copy.setCode(dto.getCode());
        copy.setSlug(dto.getSlug());
        copy.setCollectionId(dto.getCollectionId());
        copy.setCollection(dto.getCollection());
        copy.setHeroImage(dto.getHeroImage());
        copy.setModel3dUrl(dto.getModel3dUrl());
        copy.setDisplayOrder(dto.getDisplayOrder());
        copy.setStatus(dto.getStatus());
        copy.setIsHeroRelease(dto.getIsHeroRelease());
        copy.setModelSpecs(dto.getModelSpecs());
        copy.setAudioGuide(dto.getAudioGuide());
        copy.setGalleryImages(dto.getGalleryImages());
        copy.setStoryBlocks(dto.getStoryBlocks());
        copy.setRelatedProducts(dto.getRelatedProducts());

        copy.setName(map.getOrDefault("name", dto.getName()));
        copy.setSubName(map.getOrDefault("subName", dto.getSubName()));
        copy.setShortDescription(map.getOrDefault("shortDescription", dto.getShortDescription()));
        copy.setEpoch(map.getOrDefault("epoch", dto.getEpoch()));
        copy.setLocation(map.getOrDefault("location", dto.getLocation()));
        return copy;
    }

    private List<StoryBlockDto> localizeStoryBlocks(List<StoryBlockDto> blocks, String lang) {
        if (blocks == null || blocks.isEmpty() || isVietnamese(lang)) return blocks;
        List<String> ids = blocks.stream().map(StoryBlockDto::getId).collect(Collectors.toList());
        List<ContentTranslationEntity> cachedList = translationRepo.findByEntityTypeAndEntityIdInAndLangCode("story_block", ids, lang);
        Map<String, String> cachedMap = cachedList.stream()
                .collect(Collectors.toMap(ContentTranslationEntity::getEntityId, ContentTranslationEntity::getFieldsJson, (a, b) -> a));

        return blocks.stream().map(block -> {
            String fieldsJson = cachedMap.get(block.getId());
            if (fieldsJson != null) {
                try {
                    Map<String, String> map = objectMapper.readValue(fieldsJson, new TypeReference<Map<String, String>>() {});
                    StoryBlockDto copy = new StoryBlockDto();
                    copy.setId(block.getId());
                    copy.setProductId(block.getProductId());
                    copy.setType(block.getType());
                    copy.setMediaUrl(block.getMediaUrl());
                    copy.setMediaCaption(block.getMediaCaption());
                    copy.setDisplayOrder(block.getDisplayOrder());
                    copy.setStatus(block.getStatus());
                    copy.setTimelineItems(block.getTimelineItems());
                    copy.setAssemblySteps(block.getAssemblySteps());

                    copy.setTitle(map.getOrDefault("title", block.getTitle()));
                    copy.setSubtitle(map.getOrDefault("subtitle", block.getSubtitle()));
                    copy.setContent(map.getOrDefault("content", block.getContent()));
                    copy.setHighlightBadge(map.getOrDefault("highlightBadge", block.getHighlightBadge()));
                    return copy;
                } catch (Exception ignored) {}
            }
            return block;
        }).collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // ASYNC AUTOMATION / ADMIN UTILS
    // -------------------------------------------------------------

    @Async
    public void autoTranslateBlogAsync(BlogPostDto dto) {
        if (dto == null || !geminiService.isConfigured()) return;
        for (String lang : SUPPORTED_LANGUAGES) {
            try {
                if (translationRepo.findByEntityTypeAndEntityIdAndLangCode("blog", dto.getId(), lang).isEmpty()) {
                    Map<String, String> source = new HashMap<>();
                    if (dto.getTitle() != null) source.put("title", dto.getTitle());
                    if (dto.getExcerpt() != null) source.put("excerpt", dto.getExcerpt());
                    if (dto.getContent() != null) source.put("content", dto.getContent());
                    if (dto.getCategory() != null) source.put("category", dto.getCategory());
                    if (dto.getReadTime() != null) source.put("readTime", dto.getReadTime());

                    Map<String, String> translated = geminiService.translateFields(source, lang, "Vietnamese Architectural Blog Post");
                    if (translated != null && !translated.isEmpty()) {
                        saveTranslation("blog", dto.getId(), lang, translated);
                        log.info("Auto-translated blog '{}' to {}", dto.getId(), lang);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed async translation for blog {} to {}: {}", dto.getId(), lang, e.getMessage());
            }
        }
    }

    @Transactional
    public void saveTranslation(String entityType, String entityId, String langCode, Map<String, String> fields) {
        try {
            String json = objectMapper.writeValueAsString(fields);
            Optional<ContentTranslationEntity> opt = translationRepo.findByEntityTypeAndEntityIdAndLangCode(entityType, entityId, langCode);
            ContentTranslationEntity entity;
            if (opt.isPresent()) {
                entity = opt.get();
                entity.setFieldsJson(json);
            } else {
                entity = new ContentTranslationEntity(entityType, entityId, langCode, json);
            }
            translationRepo.save(entity);
        } catch (Exception e) {
            log.error("Failed to save translation for {}-{}-{}: {}", entityType, entityId, langCode, e.getMessage());
        }
    }

    @Transactional
    public void invalidateTranslation(String entityType, String entityId) {
        translationRepo.deleteByEntityTypeAndEntityId(entityType, entityId);
    }

    private boolean isVietnamese(String lang) {
        return lang == null || "vi".equalsIgnoreCase(lang.trim()) || lang.trim().isEmpty();
    }
}
