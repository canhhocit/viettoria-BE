package com.viettoria.controller;

import com.viettoria.dto.*;
import com.viettoria.service.HeritageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final HeritageService heritageService;

    public PublicController(HeritageService heritageService) {
        this.heritageService = heritageService;
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFeatured() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getFeatured()));
    }

    @GetMapping("/collections")
    public ResponseEntity<ApiResponse<List<CollectionDto>>> getCollections() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getPublishedCollections()));
    }

    @GetMapping("/collections/{slug}")
    public ResponseEntity<ApiResponse<CollectionDto>> getCollectionBySlug(@PathVariable String slug) {
        CollectionDto dto = heritageService.getCollectionBySlug(slug);
        if (dto == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Không tìm thấy bộ sưu tập"));
        }
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts(
            @RequestParam(required = false) String collectionId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getPublishedProducts(collectionId, search)));
    }

    @GetMapping("/products/{slugOrCode}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySlugOrCode(@PathVariable String slugOrCode) {
        ProductDto dto = heritageService.getProductBySlugOrCode(slugOrCode);
        if (dto == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Không tìm thấy tác phẩm di sản"));
        }
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/qr-resolve")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resolveQR(@RequestParam(required = false) String code) {
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Vui lòng cung cấp mã QR (VD: code=VT-HN-001)"));
        }
        Map<String, Object> resolved = heritageService.resolveQR(code);
        if (resolved == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Mã tem QR không tồn tại trong hệ thống di sản"));
        }
        return ResponseEntity.ok(ApiResponse.ok(resolved));
    }

    @GetMapping("/blogs")
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getBlogs() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getPublishedBlogs()));
    }

    @GetMapping("/blogs/{slug}")
    public ResponseEntity<ApiResponse<BlogPostDto>> getBlogBySlug(@PathVariable String slug) {
        BlogPostDto dto = heritageService.getBlogBySlug(slug);
        if (dto == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Không tìm thấy bài viết khảo cứu"));
        }
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
