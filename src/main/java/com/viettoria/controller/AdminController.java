package com.viettoria.controller;

import com.viettoria.dto.*;
import com.viettoria.service.CloudinaryUploadService;
import com.viettoria.service.HeritageService;
import com.viettoria.service.MediaStorageService;
import com.viettoria.service.MediaStorageService.StoredMedia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "Đăng nhập quản trị", description = "Xác thực tài khoản quản trị bằng username và password, trả về JWT để gọi các API admin. API này không yêu cầu JWT.", tags = "Authentication")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> login(@RequestBody AuthDto.LoginRequest request) {
        try {
            AuthDto.LoginResponse response = heritageService.login(request);
            return ResponseEntity.ok(ApiResponse.ok(response, "Đăng nhập thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/auth/demo")
    @Operation(summary = "Lấy phiên đăng nhập demo", description = "Tạo JWT demo để vào trang quản trị phục vụ trình diễn. Phiên demo chỉ có quyền đọc dữ liệu và không yêu cầu JWT đầu vào; có thể trả 404 nếu DEMO_LOGIN_ENABLED đang tắt.", tags = "Authentication")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> loginDemo() {
        if (!demoLoginEnabled) {
            return ResponseEntity.status(404).body(ApiResponse.error("Đăng nhập demo hiện đang tắt."));
        }
        return ResponseEntity.ok(ApiResponse.ok(heritageService.loginDemo(), "Đã vào chế độ xem thử."));
    }

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    @Operation(summary = "Lấy số liệu dashboard", description = "Trả về các số liệu tổng quan hiện có cho trang quản trị. Yêu cầu JWT admin.", tags = "Operations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.getDashboardStats()));
    }

    // --- PRODUCTS ---
    @GetMapping("/products")
    @Operation(summary = "Danh sách tác phẩm trong quản trị", description = "Lấy toàn bộ tác phẩm để quản trị, bao gồm các trạng thái nội dung. Yêu cầu JWT admin.", tags = "Products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetProducts()));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Chi tiết tác phẩm theo ID", description = "Tìm tác phẩm bằng ID nội bộ để xem hoặc chỉnh sửa. Trả 404 nếu không tồn tại. Yêu cầu JWT admin.", tags = "Products")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@Parameter(description = "ID nội bộ của tác phẩm", example = "65f1a8b2c3d4e5f678901234") @PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetProductById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/products")
    @Operation(summary = "Tạo tác phẩm", description = "Tạo bản ghi tác phẩm từ ProductDto và trả về dữ liệu vừa tạo (HTTP 201). Yêu cầu JWT admin.", tags = "Products")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@RequestBody ProductDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateProduct(dto), "Tạo sản phẩm thành công"));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Cập nhật tác phẩm", description = "Cập nhật dữ liệu tác phẩm theo ID bằng ProductDto và trả về bản ghi sau cập nhật. Yêu cầu JWT admin.", tags = "Products")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@Parameter(description = "ID nội bộ của tác phẩm") @PathVariable String id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateProduct(id, dto), "Cập nhật sản phẩm thành công"));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Xóa tác phẩm", description = "Xóa tác phẩm theo ID và trả về thông báo kết quả. Yêu cầu JWT admin.", tags = "Products")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@Parameter(description = "ID nội bộ của tác phẩm") @PathVariable String id) {
        heritageService.adminDeleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa sản phẩm thành công"));
    }

    // --- STORY BLOCKS ---
    @GetMapping("/products/{productId}/stories")
    @Operation(summary = "Danh sách khối nội dung của tác phẩm", description = "Lấy các StoryBlock đang gắn với tác phẩm; thứ tự trả về được dùng để dựng nội dung câu chuyện. Yêu cầu JWT admin.", tags = "Story Blocks")
    public ResponseEntity<ApiResponse<List<StoryBlockDto>>> getStories(@Parameter(description = "ID nội bộ của tác phẩm") @PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetStories(productId)));
    }

    @PostMapping("/products/{productId}/stories")
    @Operation(summary = "Thêm khối nội dung cho tác phẩm", description = "Tạo một StoryBlock mới và gắn vào tác phẩm được chỉ định (HTTP 201). Yêu cầu JWT admin.", tags = "Story Blocks")
    public ResponseEntity<ApiResponse<StoryBlockDto>> createStory(
            @Parameter(description = "ID nội bộ của tác phẩm") @PathVariable String productId,
            @RequestBody StoryBlockDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateStory(productId, dto), "Thêm StoryBlock thành công"));
    }

    @PutMapping("/products/{productId}/stories/reorder")
    @Operation(summary = "Sắp xếp lại các khối nội dung", description = "Nhận body có trường blockIds là danh sách ID theo thứ tự mong muốn, rồi cập nhật thứ tự StoryBlock của tác phẩm. Yêu cầu JWT admin.", tags = "Story Blocks")
    public ResponseEntity<ApiResponse<Void>> reorderStories(
            @Parameter(description = "ID nội bộ của tác phẩm") @PathVariable String productId,
            @RequestBody Map<String, List<String>> body) {
        List<String> blockIds = body.get("blockIds");
        if (blockIds != null) {
            heritageService.adminReorderStories(productId, blockIds);
        }
        return ResponseEntity.ok(ApiResponse.ok(null, "Sắp xếp lại các khối thành công"));
    }

    @PutMapping("/stories/{id}")
    @Operation(summary = "Cập nhật khối nội dung", description = "Cập nhật nội dung hoặc thuộc tính của một StoryBlock bằng ID. Yêu cầu JWT admin.", tags = "Story Blocks")
    public ResponseEntity<ApiResponse<StoryBlockDto>> updateStory(@Parameter(description = "ID nội bộ của StoryBlock") @PathVariable String id, @RequestBody StoryBlockDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateStory(id, dto), "Cập nhật StoryBlock thành công"));
    }

    @DeleteMapping("/stories/{id}")
    @Operation(summary = "Xóa khối nội dung", description = "Gỡ StoryBlock khỏi nội dung tác phẩm theo ID. Yêu cầu JWT admin.", tags = "Story Blocks")
    public ResponseEntity<ApiResponse<Void>> deleteStory(@Parameter(description = "ID nội bộ của StoryBlock") @PathVariable String id) {
        heritageService.adminDeleteStory(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa StoryBlock thành công"));
    }

    // --- COLLECTIONS ---
    @GetMapping("/collections")
    @Operation(summary = "Danh sách bộ sưu tập quản trị", description = "Lấy danh sách bộ sưu tập ở mọi trạng thái để quản trị. Yêu cầu JWT admin.", tags = "Collections")
    public ResponseEntity<ApiResponse<List<CollectionDto>>> getCollections() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetCollections()));
    }

    @PostMapping("/collections")
    @Operation(summary = "Tạo bộ sưu tập", description = "Tạo bộ sưu tập mới từ CollectionDto và trả về dữ liệu vừa tạo (HTTP 201). Yêu cầu JWT admin.", tags = "Collections")
    public ResponseEntity<ApiResponse<CollectionDto>> createCollection(@RequestBody CollectionDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateCollection(dto), "Tạo bộ sưu tập thành công"));
    }

    @PutMapping("/collections/{id}")
    @Operation(summary = "Cập nhật bộ sưu tập", description = "Cập nhật thông tin bộ sưu tập theo ID và trả về bản ghi mới. Yêu cầu JWT admin.", tags = "Collections")
    public ResponseEntity<ApiResponse<CollectionDto>> updateCollection(@Parameter(description = "ID nội bộ của bộ sưu tập") @PathVariable String id, @RequestBody CollectionDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateCollection(id, dto), "Cập nhật bộ sưu tập thành công"));
    }

    @DeleteMapping("/collections/{id}")
    @Operation(summary = "Xóa bộ sưu tập", description = "Xóa bộ sưu tập theo ID. Yêu cầu JWT admin.", tags = "Collections")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(@Parameter(description = "ID nội bộ của bộ sưu tập") @PathVariable String id) {
        heritageService.adminDeleteCollection(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa bộ sưu tập thành công"));
    }

    // --- BLOGS ---
    @GetMapping("/blogs")
    @Operation(summary = "Danh sách bài viết quản trị", description = "Lấy toàn bộ bài viết khảo cứu để quản trị, gồm cả nội dung chưa xuất bản. Yêu cầu JWT admin.", tags = "Blogs")
    public ResponseEntity<ApiResponse<List<BlogPostDto>>> getBlogs() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetBlogs()));
    }

    @PostMapping("/blogs")
    @Operation(summary = "Tạo bài viết khảo cứu", description = "Tạo bài viết mới từ BlogPostDto và trả về dữ liệu vừa tạo (HTTP 201). Yêu cầu JWT admin.", tags = "Blogs")
    public ResponseEntity<ApiResponse<BlogPostDto>> createBlog(@RequestBody BlogPostDto dto) {
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateBlog(dto), "Tạo bài viết thành công"));
    }

    @PutMapping("/blogs/{id}")
    @Operation(summary = "Cập nhật bài viết khảo cứu", description = "Cập nhật bài viết theo ID bằng BlogPostDto. Yêu cầu JWT admin.", tags = "Blogs")
    public ResponseEntity<ApiResponse<BlogPostDto>> updateBlog(@Parameter(description = "ID nội bộ của bài viết") @PathVariable String id, @RequestBody BlogPostDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminUpdateBlog(id, dto), "Cập nhật bài viết thành công"));
    }

    @DeleteMapping("/blogs/{id}")
    @Operation(summary = "Xóa bài viết khảo cứu", description = "Xóa bài viết theo ID. Yêu cầu JWT admin.", tags = "Blogs")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@Parameter(description = "ID nội bộ của bài viết") @PathVariable String id) {
        heritageService.adminDeleteBlog(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa bài viết thành công"));
    }

    // --- MEDIA ---
    @GetMapping("/media")
    @Operation(summary = "Danh sách thư viện media", description = "Lấy các ảnh, video hoặc mô hình 3D đã lưu trong thư viện quản trị. Yêu cầu JWT admin.", tags = "Media Library")
    public ResponseEntity<ApiResponse<List<MediaItemDto>>> getMedia() {
        return ResponseEntity.ok(ApiResponse.ok(heritageService.adminGetMedia()));
    }

    @PostMapping("/media/upload-signature")
    @Operation(summary = "Tạo chữ ký upload Cloudinary", description = "Tạo chữ ký có thời hạn để trình duyệt tải tệp trực tiếp lên Cloudinary mà không đưa API secret ra frontend. Body xác định loại tài nguyên và phần mở rộng tệp. Yêu cầu JWT admin.", tags = "Media Library")
    public ResponseEntity<ApiResponse<CloudinaryUploadSignatureDto>> createMediaUploadSignature(
            @RequestBody CloudinaryUploadSignatureRequest request) {
        CloudinaryUploadSignatureDto signature = cloudinaryUploadService.createUploadSignature(
                request.resourceType(), request.fileExtension());
        return ResponseEntity.ok(ApiResponse.ok(signature));
    }

    @PostMapping("/media")
    @Operation(summary = "Lưu tệp Cloudinary vào thư viện", description = "Xác thực thông tin URL, resource type và public ID của tệp đã upload lên Cloudinary, sau đó tạo bản ghi media (HTTP 201). Yêu cầu JWT admin.", tags = "Media Library")
    public ResponseEntity<ApiResponse<MediaItemDto>> saveMedia(@RequestBody MediaItemDto dto) {
        cloudinaryUploadService.validateUploadedMedia(dto.getUrl(), dto.getResourceType(), dto.getPublicId());
        return ResponseEntity.status(201).body(ApiResponse.ok(heritageService.adminCreateMedia(dto), "Đã lưu tệp Cloudinary vào thư viện."));
    }

    @PostMapping(value = "/media/fallback-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload media dự phòng lên backend", description = "Nhận multipart/form-data với file bắt buộc và name, altText tùy chọn. Dùng lưu tệp qua backend khi luồng Cloudinary không khả dụng; trả bản ghi media (HTTP 201). Yêu cầu JWT admin.", tags = "Media Library")
    public ResponseEntity<ApiResponse<MediaItemDto>> uploadMediaToBackend(
            @Parameter(description = "Tệp cần tải lên", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Tên hiển thị tùy chọn") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "Văn bản thay thế cho ảnh, nếu có") @RequestParam(value = "altText", required = false) String altText) {
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
