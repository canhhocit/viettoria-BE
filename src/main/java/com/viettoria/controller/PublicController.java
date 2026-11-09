package com.viettoria.controller;

import com.viettoria.dto.*;
import com.viettoria.service.ContentLocalizationService;
import com.viettoria.service.HeritageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final HeritageService heritageService;
    private final ContentLocalizationService localizationService;

    public PublicController(HeritageService heritageService, ContentLocalizationService localizationService) {
        this.heritageService = heritageService;
        this.localizationService = localizationService;
    }

    @GetMapping("/featured")
    @Operation(summary = "Nội dung nổi bật trang chủ", description = "Lấy dữ liệu nổi bật hiện được cấu hình để dựng trang chủ website; chỉ dành cho nội dung công khai.", tags = "Featured Content")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFeatured(
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "lang", required = false) String langParam) {
        String lang = localizationService.resolveLanguage(langParam, acceptLanguage);
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getFeatured(lang)));
    }

    @GetMapping("/collections")
    @Operation(summary = "Danh sách bộ sưu tập đã công bố", description = "Trả về các bộ sưu tập được phép hiển thị trên website; bộ sưu tập chưa công bố không xuất hiện.", tags = "Collections")
    public ResponseEntity<ApiResponse<List<CollectionDto>>> getCollections(
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "lang", required = false) String langParam) {
        String lang = localizationService.resolveLanguage(langParam, acceptLanguage);
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getPublishedCollections(lang)));
    }

    @GetMapping("/collections/{slug}")
    @Operation(summary = "Chi tiết bộ sưu tập theo slug", description = "Tìm bộ sưu tập công khai theo slug trên URL. Trả 404 nếu không tìm thấy hoặc không được công khai.", tags = "Collections")
    public ResponseEntity<ApiResponse<CollectionDto>> getCollectionBySlug(
            @Parameter(description = "Slug thân thiện với URL của bộ sưu tập", example = "kien-truc-thoi-ly") @PathVariable String slug,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "lang", required = false) String langParam) {
        String lang = localizationService.resolveLanguage(langParam, acceptLanguage);
        CollectionDto dto = heritageService.getCollectionBySlug(slug, lang);
        if (dto == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Không tìm thấy bộ sưu tập"));
        }
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/products")
    @Operation(summary = "Danh sách tác phẩm đã công bố", description = "Lấy tác phẩm công khai; có thể lọc theo collectionId và tìm kiếm theo nội dung. Bỏ trống query để lấy danh sách mặc định.", tags = "Products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts(
            @Parameter(description = "ID bộ sưu tập cần lọc") @RequestParam(required = false) String collectionId,
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String search,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "lang", required = false) String langParam) {
        String lang = localizationService.resolveLanguage(langParam, acceptLanguage);
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getPublishedProducts(collectionId, search, lang)));
    }

    @GetMapping("/products/{slugOrCode}")
    @Operation(summary = "Chi tiết tác phẩm theo slug hoặc mã", description = "Tra cứu tác phẩm công khai bằng slug hoặc mã tác phẩm. Trả 404 nếu không có tác phẩm tương ứng.", tags = "Products")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySlugOrCode(
            @Parameter(description = "Slug hoặc mã định danh tác phẩm", example = "VT-HN-001") @PathVariable String slugOrCode,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "lang", required = false) String langParam) {
        String lang = localizationService.resolveLanguage(langParam, acceptLanguage);
        ProductDto dto = heritageService.getProductBySlugOrCode(slugOrCode, lang);
        if (dto == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Không tìm thấy tác phẩm di sản"));
        }
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/qr-resolve")
    @Operation(summary = "Giải mã mã QR tác phẩm", description = "Tra mã in trên tem QR để lấy thông tin liên kết tới tác phẩm. Tham số code bắt buộc khi gọi thực tế; trả 400 nếu thiếu và 404 nếu mã chưa được đăng ký. Ví dụ: code=VT-HN-001.", tags = "QR Lookup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resolveQR(@Parameter(description = "Mã QR in trên sản phẩm", example = "VT-HN-001") @RequestParam(required = false) String code) {
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
    @Operation(summary = "Danh sách bài viết đã xuất bản", description = "Lấy các bài viết khảo cứu đang công khai trên website.", tags = "Editorial")
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getBlogs(
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "lang", required = false) String langParam) {
        String lang = localizationService.resolveLanguage(langParam, acceptLanguage);
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getPublishedBlogs(lang)));
    }

    @GetMapping("/blogs/{slug}")
    @Operation(summary = "Chi tiết bài viết theo slug", description = "Tìm bài viết khảo cứu đã xuất bản bằng slug. Trả 404 nếu không tìm thấy hoặc chưa được công khai.", tags = "Editorial")
    public ResponseEntity<ApiResponse<BlogPostDto>> getBlogBySlug(
            @Parameter(description = "Slug thân thiện với URL của bài viết", example = "dau-an-kien-truc-thoi-ly") @PathVariable String slug,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            @RequestParam(value = "lang", required = false) String langParam) {
        String lang = localizationService.resolveLanguage(langParam, acceptLanguage);
        BlogPostDto dto = heritageService.getBlogBySlug(slug, lang);
        if (dto == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Không tìm thấy bài viết khảo cứu"));
        }
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
