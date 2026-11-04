package com.viettoria.controller;

import com.viettoria.dto.*;
import com.viettoria.service.CloudinaryUploadService;
import com.viettoria.service.HeritageService;
import com.viettoria.service.MediaStorageService;
import com.viettoria.service.MediaStorageService.StoredMedia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final HeritageService heritageService;
    private final CloudinaryUploadService cloudinaryUploadService;
    private final MediaStorageService mediaStorageService;
    private final boolean demoLoginEnabled;

    public AdminController(
            HeritageService heritageService,
            CloudinaryUploadService cloudinaryUploadService,
            MediaStorageService mediaStorageService,
            @Value("${DEMO_LOGIN_ENABLED:true}") boolean demoLoginEnabled) {
        this.heritageService = heritageService;
        this.cloudinaryUploadService = cloudinaryUploadService;
        this.mediaStorageService = mediaStorageService;
        this.demoLoginEnabled = demoLoginEnabled;
    }

    // --- AUTH ---
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> login(@RequestBody AuthDto.LoginRequest request) {
        try {
            AuthDto.LoginResponse response = heritageService.login(request);
            return ResponseEntity.ok(ApiResponse.ok(response, "Đăng nhập thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/auth/demo")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> loginDemo() {
        if (!demoLoginEnabled) {
            return ResponseEntity.status(404).body(ApiResponse.error("Đăng nhập demo hiện đang tắt."));
        }
        return ResponseEntity.ok(ApiResponse.ok(heritageService.loginDemo(), "Đã vào chế độ xem thử."));
    }

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getDashboardStats()));
    }

    // --- PRODUCTS ---
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetProducts()));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetProductById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@RequestBody ProductDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateProduct(dto), "Tạo sản phẩm thành công"));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@PathVariable String id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateProduct(id, dto), "Cập nhật sản phẩm thành công"));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        heritageService.adminDeleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa sản phẩm thành công"));
    }

    // --- STORY BLOCKS ---
    @GetMapping("/products/{productId}/stories")
    public ResponseEntity<ApiResponse<List<StoryBlockDto>>> getStories(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetStories(productId)));
    }

    @PostMapping("/products/{productId}/stories")
    public ResponseEntity<ApiResponse<StoryBlockDto>> createStory(
            @PathVariable String productId,
            @RequestBody StoryBlockDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateStory(productId, dto), "Thêm StoryBlock thành công"));
    }

    @PutMapping("/products/{productId}/stories/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderStories(
            @PathVariable String productId,
            @RequestBody Map<String, List<String>> body) {
        List<String> blockIds = body.get("blockIds");
        if (blockIds != null) {
            heritageService.adminReorderStories(productId, blockIds);
        }
        return ResponseEntity.ok(ApiResponse.ok(null, "Sắp xếp lại các khối thành công"));
    }

    @PutMapping("/stories/{id}")
    public ResponseEntity<ApiResponse<StoryBlockDto>> updateStory(@PathVariable String id, @RequestBody StoryBlockDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateStory(id, dto), "Cập nhật StoryBlock thành công"));
    }

    @DeleteMapping("/stories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStory(@PathVariable String id) {
        heritageService.adminDeleteStory(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa StoryBlock thành công"));
    }

    // --- COLLECTIONS ---
    @GetMapping("/collections")
    public ResponseEntity<ApiResponse<List<CollectionDto>>> getCollections() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetCollections()));
    }

    @PostMapping("/collections")
    public ResponseEntity<ApiResponse<CollectionDto>> createCollection(@RequestBody CollectionDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateCollection(dto), "Tạo bộ sưu tập thành công"));
    }

    @PutMapping("/collections/{id}")
    public ResponseEntity<ApiResponse<CollectionDto>> updateCollection(@PathVariable String id, @RequestBody CollectionDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateCollection(id, dto), "Cập nhật bộ sưu tập thành công"));
    }

    @DeleteMapping("/collections/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(@PathVariable String id) {
        heritageService.adminDeleteCollection(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa bộ sưu tập thành công"));
    }

    // --- BLOGS ---
    @GetMapping("/blogs")
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getBlogs() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetBlogs()));
    }

    @PostMapping("/blogs")
    public ResponseEntity<ApiResponse<BlogPostDto>> createBlog(@RequestBody BlogPostDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateBlog(dto), "Tạo bài viết thành công"));
    }

    @PutMapping("/blogs/{id}")
    public ResponseEntity<ApiResponse<BlogPostDto>> updateBlog(@PathVariable String id, @RequestBody BlogPostDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateBlog(id, dto), "Cập nhật bài viết thành công"));
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@PathVariable String id) {
        heritageService.adminDeleteBlog(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa bài viết thành công"));
    }

    // --- MEDIA ---
    @GetMapping("/media")
    public ResponseEntity<ApiResponse<List<MediaItemDto>>> getMedia() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetMedia()));
    }

    @PostMapping("/media/upload-signature")
    public ResponseEntity<ApiResponse<CloudinaryUploadSignatureDto>> createMediaUploadSignature(
            @RequestBody CloudinaryUploadSignatureRequest request) {
        CloudinaryUploadSignatureDto signature = cloudinaryUploadService.createUploadSignature(
                request.resourceType(), request.fileExtension());
        return ResponseEntity.ok(ApiResponse.ok(signature));
    }

    @PostMapping("/media")
    public ResponseEntity<ApiResponse<MediaItemDto>> saveMedia(@RequestBody MediaItemDto dto) {
        cloudinaryUploadService.validateUploadedMedia(dto.getUrl(), dto.getResourceType(), dto.getPublicId());
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateMedia(dto), "Đã lưu tệp Cloudinary vào thư viện."));
    }

    @PostMapping(value = "/media/fallback-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MediaItemDto>> uploadMediaToBackend(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "altText", required = false) String altText) {
        StoredMedia stored = mediaStorageService.store(file);
        String displayName = name == null || name.isBlank() ? stored.originalName().replaceFirst("\\.[^.]+$", "") : name;
        MediaItemDto dto = new MediaItemDto();
        dto.setName(displayName);
        dto.setFileName(stored.originalName());
        dto.setFileType(stored.fileType());
        dto.setFileSize(stored.fileSize());
        dto.setUrl(stored.url());
        dto.setResourceType(stored.resourceType());
        dto.setAltText(altText);
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateMedia(dto), "Cloudinary chưa khả dụng; tệp đã được lưu trên backend."));
    }
}
