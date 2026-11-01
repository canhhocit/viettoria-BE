package com.viettoria.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettoria.dto.*;
import com.viettoria.entity.*;
import com.viettoria.repository.*;
import com.viettoria.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HeritageService {

    private final CollectionRepository collectionRepo;
    private final ProductRepository productRepo;
    private final StoryBlockRepository storyBlockRepo;
    private final BlogPostRepository blogPostRepo;
    private final MediaItemRepository mediaItemRepo;
    private final AdminUserRepository adminUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public HeritageService(
            CollectionRepository collectionRepo,
            ProductRepository productRepo,
            StoryBlockRepository storyBlockRepo,
            BlogPostRepository blogPostRepo,
            MediaItemRepository mediaItemRepo,
            AdminUserRepository adminUserRepo,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            ObjectMapper objectMapper) {
        this.collectionRepo = collectionRepo;
        this.productRepo = productRepo;
        this.storyBlockRepo = storyBlockRepo;
        this.blogPostRepo = blogPostRepo;
        this.mediaItemRepo = mediaItemRepo;
        this.adminUserRepo = adminUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    // --- PUBLIC METHODS ---

    @Transactional(readOnly = true)
    public List<CollectionDto> getPublishedCollections() {
        return collectionRepo.findByStatusOrderByDisplayOrderAsc("published").stream()
                .map(this::mapCollectionToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CollectionDto getCollectionBySlug(String slug) {
        CollectionEntity entity = collectionRepo.findBySlug(slug).orElse(null);
        if (entity == null) return null;

        CollectionDto dto = mapCollectionToDto(entity);
        List<ProductDto> products = productRepo.findByCollectionIdAndStatusOrderByDisplayOrderAsc(entity.getId(), "published").stream()
                .map(this::mapProductToDto)
                .collect(Collectors.toList());
        dto.setProducts(products);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getPublishedProducts(String collectionId, String search) {
        List<ProductEntity> list = productRepo.findByStatusOrderByDisplayOrderAsc("published");

        if (collectionId != null && !collectionId.isBlank() && !"all".equalsIgnoreCase(collectionId)) {
            list = list.stream().filter(p -> collectionId.equals(p.getCollectionId())).collect(Collectors.toList());
        }

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            list = list.stream().filter(p ->
                    p.getName().toLowerCase().contains(q) ||
                    p.getCode().toLowerCase().contains(q) ||
                    (p.getLocation() != null && p.getLocation().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }

        return list.stream().map(this::mapProductToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductBySlugOrCode(String slugOrCode) {
        ProductEntity entity = productRepo.findBySlugOrCode(slugOrCode).orElse(null);
        if (entity == null) return null;

        ProductDto dto = mapProductToDto(entity);

        // Fetch collection
        if (entity.getCollectionId() != null) {
            collectionRepo.findById(entity.getCollectionId()).ifPresent(c -> dto.setCollection(mapCollectionToDto(c)));
        }

        // Fetch published story blocks
        List<StoryBlockDto> storyBlocks = storyBlockRepo.findByProductIdAndStatusOrderByDisplayOrderAsc(entity.getId(), "published").stream()
                .map(this::mapStoryBlockToDto)
                .collect(Collectors.toList());
        dto.setStoryBlocks(storyBlocks);

        // Related products in same collection
        if (entity.getCollectionId() != null) {
            List<ProductDto> related = productRepo.findByCollectionIdAndStatusOrderByDisplayOrderAsc(entity.getCollectionId(), "published").stream()
                    .filter(p -> !p.getId().equals(entity.getId()))
                    .limit(3)
                    .map(this::mapProductToDto)
                    .collect(Collectors.toList());
            dto.setRelatedProducts(related);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> resolveQR(String code) {
        ProductEntity product = productRepo.findByCodeIgnoreCase(code.trim()).orElse(null);
        if (product == null) return null;

        Map<String, Object> res = new HashMap<>();
        res.put("code", product.getCode());
        res.put("slug", product.getSlug());
        res.put("targetUrl", "/products/" + product.getSlug() + "?code=" + product.getCode());
        res.put("product", mapProductToDto(product));
        return res;
    }

    @Transactional(readOnly = true)
    public List<BlogPostDto> getPublishedBlogs() {
        return blogPostRepo.findByStatusOrderByPublishedAtDesc("published").stream()
                .map(this::mapBlogToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BlogPostDto getBlogBySlug(String slug) {
        return blogPostRepo.findBySlug(slug).map(this::mapBlogToDto).orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFeatured() {
        List<ProductEntity> published = productRepo.findByStatusOrderByDisplayOrderAsc("published");
        ProductEntity hero = published.stream().filter(ProductEntity::isHeroRelease).findFirst()
                .orElse(published.isEmpty() ? null : published.get(0));

        List<ProductDto> featuredProducts = published.stream()
                .filter(p -> hero == null || !p.getId().equals(hero.getId()))
                .limit(4)
                .map(this::mapProductToDto)
                .collect(Collectors.toList());

        List<CollectionDto> collections = collectionRepo.findByStatusOrderByDisplayOrderAsc("published").stream()
                .limit(3)
                .map(this::mapCollectionToDto)
                .collect(Collectors.toList());

        List<BlogPostDto> latestBlogs = blogPostRepo.findByStatusOrderByPublishedAtDesc("published").stream()
                .limit(3)
                .map(this::mapBlogToDto)
                .collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("heroProduct", hero != null ? mapProductToDto(hero) : null);
        res.put("featuredProducts", featuredProducts);
        res.put("collections", collections);
        res.put("latestBlogs", latestBlogs);
        return res;
    }

    // --- ADMIN METHODS ---

    @Transactional(readOnly = true)
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        AdminUserEntity user = adminUserRepo.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole());
        AuthDto.AdminUserDto userDto = new AuthDto.AdminUserDto(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
        return new AuthDto.LoginResponse(token, userDto);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        long totalProducts = productRepo.count();
        long publishedProducts = productRepo.countByStatus("published");
        long totalCollections = collectionRepo.count();
        long totalBlogs = blogPostRepo.count();
        long totalStoryBlocks = storyBlockRepo.count();
        long totalMedia = mediaItemRepo.count();

        DashboardStatsDto stats = new DashboardStatsDto(
                totalProducts, publishedProducts, totalCollections, totalBlogs, totalStoryBlocks, totalMedia
        );

        List<ProductDto> recentProducts = productRepo.findAllByOrderByDisplayOrderAsc().stream()
                .limit(5)
                .map(this::mapProductToDto)
                .collect(Collectors.toList());

        List<BlogPostDto> recentBlogs = blogPostRepo.findAllByOrderByPublishedAtDesc().stream()
                .limit(5)
                .map(this::mapBlogToDto)
                .collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("stats", stats);
        res.put("recentProducts", recentProducts);
        res.put("recentBlogs", recentBlogs);
        return res;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> adminGetProducts() {
        return productRepo.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapProductToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto adminGetProductById(String id) {
        ProductEntity entity = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDto dto = mapProductToDto(entity);
        List<StoryBlockDto> storyBlocks = storyBlockRepo.findByProductIdOrderByDisplayOrderAsc(entity.getId()).stream()
                .map(this::mapStoryBlockToDto)
                .collect(Collectors.toList());
        dto.setStoryBlocks(storyBlocks);
        return dto;
    }

    @Transactional
    public ProductDto adminCreateProduct(ProductDto dto) {
        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId() != null ? dto.getId() : "prod-" + UUID.randomUUID().toString().substring(0, 8));
        updateProductEntityFromDto(entity, dto);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return mapProductToDto(productRepo.save(entity));
    }

    @Transactional
    public ProductDto adminUpdateProduct(String id, ProductDto dto) {
        ProductEntity entity = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        updateProductEntityFromDto(entity, dto);
        entity.setUpdatedAt(Instant.now());
        return mapProductToDto(productRepo.save(entity));
    }

    @Transactional
    public void adminDeleteProduct(String id) {
        storyBlockRepo.deleteByProductId(id);
        productRepo.deleteById(id);
    }

    // Story Blocks Admin
    @Transactional(readOnly = true)
    public List<StoryBlockDto> adminGetStories(String productId) {
        return storyBlockRepo.findByProductIdOrderByDisplayOrderAsc(productId).stream()
                .map(this::mapStoryBlockToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoryBlockDto adminCreateStory(String productId, StoryBlockDto dto) {
        StoryBlockEntity entity = new StoryBlockEntity();
        entity.setId(dto.getId() != null ? dto.getId() : "block-" + UUID.randomUUID().toString().substring(0, 8));
        entity.setProductId(productId);
        updateStoryBlockEntityFromDto(entity, dto);
        return mapStoryBlockToDto(storyBlockRepo.save(entity));
    }

    @Transactional
    public StoryBlockDto adminUpdateStory(String id, StoryBlockDto dto) {
        StoryBlockEntity entity = storyBlockRepo.findById(id).orElseThrow(() -> new RuntimeException("StoryBlock not found"));
        updateStoryBlockEntityFromDto(entity, dto);
        return mapStoryBlockToDto(storyBlockRepo.save(entity));
    }

    @Transactional
    public void adminDeleteStory(String id) {
        storyBlockRepo.deleteById(id);
    }

    @Transactional
    public void adminReorderStories(String productId, List<String> blockIds) {
        for (int i = 0; i < blockIds.size(); i++) {
            String blockId = blockIds.get(i);
            int order = i + 1;
            storyBlockRepo.findById(blockId).ifPresent(block -> {
                block.setDisplayOrder(order);
                storyBlockRepo.save(block);
            });
        }
    }

    // Collections Admin
    @Transactional(readOnly = true)
    public List<CollectionDto> adminGetCollections() {
        return collectionRepo.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapCollectionToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CollectionDto adminCreateCollection(CollectionDto dto) {
        CollectionEntity entity = new CollectionEntity();
        entity.setId(dto.getId() != null ? dto.getId() : "col-" + UUID.randomUUID().toString().substring(0, 8));
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setHeroImage(dto.getHeroImage());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "published");
        return mapCollectionToDto(collectionRepo.save(entity));
    }

    @Transactional
    public CollectionDto adminUpdateCollection(String id, CollectionDto dto) {
        CollectionEntity entity = collectionRepo.findById(id).orElseThrow(() -> new RuntimeException("Collection not found"));
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getSlug() != null) entity.setSlug(dto.getSlug());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getHeroImage() != null) entity.setHeroImage(dto.getHeroImage());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setUpdatedAt(Instant.now());
        return mapCollectionToDto(collectionRepo.save(entity));
    }

    @Transactional
    public void adminDeleteCollection(String id) {
        collectionRepo.deleteById(id);
    }

    // Blogs Admin
    @Transactional(readOnly = true)
    public List<BlogPostDto> adminGetBlogs() {
        return blogPostRepo.findAllByOrderByPublishedAtDesc().stream()
                .map(this::mapBlogToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BlogPostDto adminCreateBlog(BlogPostDto dto) {
        BlogPostEntity entity = new BlogPostEntity();
        entity.setId(dto.getId() != null ? dto.getId() : "blog-" + UUID.randomUUID().toString().substring(0, 8));
        entity.setTitle(dto.getTitle());
        entity.setSlug(dto.getSlug());
        entity.setExcerpt(dto.getExcerpt());
        entity.setContent(dto.getContent());
        entity.setCoverImage(dto.getCoverImage());
        entity.setAuthor(dto.getAuthor());
        entity.setCategory(dto.getCategory());
        entity.setReadTime(dto.getReadTime());
        entity.setPublishedAt(dto.getPublishedAt() != null ? dto.getPublishedAt() : "2026-03-30");
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "published");
        return mapBlogToDto(blogPostRepo.save(entity));
    }

    @Transactional
    public BlogPostDto adminUpdateBlog(String id, BlogPostDto dto) {
        BlogPostEntity entity = blogPostRepo.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getSlug() != null) entity.setSlug(dto.getSlug());
        if (dto.getExcerpt() != null) entity.setExcerpt(dto.getExcerpt());
        if (dto.getContent() != null) entity.setContent(dto.getContent());
        if (dto.getCoverImage() != null) entity.setCoverImage(dto.getCoverImage());
        if (dto.getAuthor() != null) entity.setAuthor(dto.getAuthor());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getReadTime() != null) entity.setReadTime(dto.getReadTime());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        return mapBlogToDto(blogPostRepo.save(entity));
    }

    @Transactional
    public void adminDeleteBlog(String id) {
        blogPostRepo.deleteById(id);
    }

    // Media Admin
    @Transactional(readOnly = true)
    public List<MediaItemDto> adminGetMedia() {
        return mediaItemRepo.findAllByOrderByUploadedAtDesc().stream()
                .map(this::mapMediaToDto)
                .collect(Collectors.toList());
    }

    // --- MAPPERS ---

    private CollectionDto mapCollectionToDto(CollectionEntity c) {
        CollectionDto dto = new CollectionDto();
        dto.setId(c.getId());
        dto.setCode(c.getCode());
        dto.setName(c.getName());
        dto.setSlug(c.getSlug());
        dto.setDescription(c.getDescription());
        dto.setHeroImage(c.getHeroImage());
        dto.setDisplayOrder(c.getDisplayOrder());
        dto.setStatus(c.getStatus());
        long count = productRepo.findByCollectionId(c.getId()).size();
        dto.setProductsCount((int) count);
        return dto;
    }

    private ProductDto mapProductToDto(ProductEntity p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setCode(p.getCode());
        dto.setName(p.getName());
        dto.setSubName(p.getSubName());
        dto.setSlug(p.getSlug());
        dto.setCollectionId(p.getCollectionId());
        dto.setEpoch(p.getEpoch());
        dto.setLocation(p.getLocation());
        dto.setShortDescription(p.getShortDescription());

        ModelSpecsDto specs = new ModelSpecsDto(
                p.getScaleRatio(), p.getHeightCm(), p.getWidthCm(), p.getWeightGrams(),
                p.getMaterial(), p.getPartsCount(), p.getFinishing()
        );
        dto.setModelSpecs(specs);

        dto.setHeroImage(p.getHeroImage());

        // Parse gallery json
        List<String> gallery = new ArrayList<>();
        try {
            if (p.getGalleryImagesJson() != null && !p.getGalleryImagesJson().isBlank()) {
                gallery = objectMapper.readValue(p.getGalleryImagesJson(), new TypeReference<List<String>>() {});
            }
        } catch (Exception ignored) {}
        dto.setGalleryImages(gallery);

        dto.setModel3dUrl(p.getModel3dUrl());

        if (p.getAudioTitle() != null || p.getAudioUrl() != null) {
            AudioGuideDto audio = new AudioGuideDto(
                    p.getAudioTitle(), p.getAudioDuration(), p.getAudioUrl(), p.getAudioNarrator(), p.getAudioTranscript()
            );
            dto.setAudioGuide(audio);
        }

        dto.setDisplayOrder(p.getDisplayOrder());
        dto.setStatus(p.getStatus());
        dto.setIsHeroRelease(p.isHeroRelease());
        return dto;
    }

    private void updateProductEntityFromDto(ProductEntity entity, ProductDto dto) {
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getSubName() != null) entity.setSubName(dto.getSubName());
        if (dto.getSlug() != null) entity.setSlug(dto.getSlug());
        if (dto.getCollectionId() != null) entity.setCollectionId(dto.getCollectionId());
        if (dto.getEpoch() != null) entity.setEpoch(dto.getEpoch());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getShortDescription() != null) entity.setShortDescription(dto.getShortDescription());

        if (dto.getModelSpecs() != null) {
            ModelSpecsDto s = dto.getModelSpecs();
            if (s.getScale() != null) entity.setScaleRatio(s.getScale());
            if (s.getHeightCm() > 0) entity.setHeightCm(s.getHeightCm());
            if (s.getWidthCm() > 0) entity.setWidthCm(s.getWidthCm());
            if (s.getWeightGrams() > 0) entity.setWeightGrams(s.getWeightGrams());
            if (s.getMaterial() != null) entity.setMaterial(s.getMaterial());
            if (s.getPartsCount() > 0) entity.setPartsCount(s.getPartsCount());
            if (s.getFinishing() != null) entity.setFinishing(s.getFinishing());
        }

        if (dto.getHeroImage() != null) entity.setHeroImage(dto.getHeroImage());

        if (dto.getGalleryImages() != null) {
            try {
                entity.setGalleryImagesJson(objectMapper.writeValueAsString(dto.getGalleryImages()));
            } catch (Exception ignored) {}
        }

        if (dto.getModel3dUrl() != null) entity.setModel3dUrl(dto.getModel3dUrl());

        if (dto.getAudioGuide() != null) {
            AudioGuideDto a = dto.getAudioGuide();
            if (a.getTitle() != null) entity.setAudioTitle(a.getTitle());
            if (a.getDuration() != null) entity.setAudioDuration(a.getDuration());
            if (a.getAudioUrl() != null) entity.setAudioUrl(a.getAudioUrl());
            if (a.getNarrator() != null) entity.setAudioNarrator(a.getNarrator());
            if (a.getTranscript() != null) entity.setAudioTranscript(a.getTranscript());
        }

        entity.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getIsHeroRelease() != null) entity.setHeroRelease(dto.getIsHeroRelease());
    }

    private StoryBlockDto mapStoryBlockToDto(StoryBlockEntity b) {
        StoryBlockDto dto = new StoryBlockDto();
        dto.setId(b.getId());
        dto.setProductId(b.getProductId());
        dto.setType(b.getType());
        dto.setTitle(b.getTitle());
        dto.setSubtitle(b.getSubtitle());
        dto.setContent(b.getContent());
        dto.setMediaUrl(b.getMediaUrl());
        dto.setMediaCaption(b.getMediaCaption());

        if (b.getTimelineItemsJson() != null && !b.getTimelineItemsJson().isBlank()) {
            try {
                dto.setTimelineItems(objectMapper.readValue(b.getTimelineItemsJson(), Object.class));
            } catch (Exception ignored) {}
        }

        if (b.getAssemblyStepsJson() != null && !b.getAssemblyStepsJson().isBlank()) {
            try {
                dto.setAssemblySteps(objectMapper.readValue(b.getAssemblyStepsJson(), Object.class));
            } catch (Exception ignored) {}
        }

        dto.setHighlightBadge(b.getHighlightBadge());
        dto.setDisplayOrder(b.getDisplayOrder());
        dto.setStatus(b.getStatus());
        return dto;
    }

    private void updateStoryBlockEntityFromDto(StoryBlockEntity entity, StoryBlockDto dto) {
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getSubtitle() != null) entity.setSubtitle(dto.getSubtitle());
        if (dto.getContent() != null) entity.setContent(dto.getContent());
        if (dto.getMediaUrl() != null) entity.setMediaUrl(dto.getMediaUrl());
        if (dto.getMediaCaption() != null) entity.setMediaCaption(dto.getMediaCaption());

        if (dto.getTimelineItems() != null) {
            try {
                entity.setTimelineItemsJson(objectMapper.writeValueAsString(dto.getTimelineItems()));
            } catch (Exception ignored) {}
        }

        if (dto.getAssemblySteps() != null) {
            try {
                entity.setAssemblyStepsJson(objectMapper.writeValueAsString(dto.getAssemblySteps()));
            } catch (Exception ignored) {}
        }

        if (dto.getHighlightBadge() != null) entity.setHighlightBadge(dto.getHighlightBadge());
        entity.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
    }

    private BlogPostDto mapBlogToDto(BlogPostEntity b) {
        BlogPostDto dto = new BlogPostDto();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setSlug(b.getSlug());
        dto.setExcerpt(b.getExcerpt());
        dto.setContent(b.getContent());
        dto.setCoverImage(b.getCoverImage());
        dto.setAuthor(b.getAuthor());
        dto.setCategory(b.getCategory());
        dto.setReadTime(b.getReadTime());
        dto.setPublishedAt(b.getPublishedAt());
        dto.setStatus(b.getStatus());
        return dto;
    }

    private MediaItemDto mapMediaToDto(MediaItemEntity m) {
        MediaItemDto dto = new MediaItemDto();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setFileName(m.getFileName());
        dto.setFileType(m.getFileType());
        dto.setFileSize(m.getFileSize());
        dto.setUrl(m.getUrl());
        dto.setAltText(m.getAltText());
        dto.setUploadedAt(m.getUploadedAt());
        return dto;
    }
}
