package com.viettoria.controller;

import com.viettoria.dto.*;
import com.viettoria.service.HeritageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final HeritageService heritageService;

    public AdminController(HeritageService heritageService) {
        this.heritageService = heritageService;
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
}
