# VIETTORIA — Spring Boot Backend & CMS Engine

Backend REST API và Hệ thống Quản trị Nội dung (CMS) cho nền tảng di sản kiến trúc **VIETTORIA**.

## Công nghệ sử dụng
- **Java**: 17 LTS (Oracle JDK / OpenJDK)
- **Spring Boot**: 3.4.2
- **Spring Data JPA & Hibernate**: Quản lý truy vấn ORM
- **Spring Security 6 & JJWT 0.12.6**: Xác thực phi trạng thái (Stateless JWT Bearer Token)
- **Cơ sở dữ liệu**: PostgreSQL (Hỗ trợ Neon Serverless Database qua tệp `.env`) / H2 fallback
- **Quản lý dự án & Build**: Apache Maven 3.9+

---

## Cấu hình Biến Môi Trường (.env)

Hệ thống tự động nạp cấu hình từ tệp `.env` tại thư mục gốc của Backend (`source/viettoria-BE/.env`).

Tệp `.env` đã được đưa vào `.gitignore` để đảm bảo an toàn thông tin mật. Mẫu cấu hình tham khảo được lưu tại `.env.example`:

```bash
# Sao chép mẫu cấu hình
cp .env.example .env
```

Các biến môi trường chính:
- `SPRING_DATASOURCE_URL`: JDBC URL kết nối cơ sở dữ liệu (`jdbc:postgresql://...`).
- `SPRING_DATASOURCE_USERNAME`: Tên người dùng database.
- `SPRING_DATASOURCE_PASSWORD`: Mật khẩu database.
- `JWT_SECRET`: Khóa ký JWT ngẫu nhiên, tối thiểu 32 byte.
- `ADMIN_USERNAME` / `ADMIN_PASSWORD`: Tài khoản quản trị được tạo khi database chưa có tài khoản admin.
- `SERVER_PORT`: Cổng máy chủ (mặc định: `8081`; có thể dùng `PORT` trên nền tảng host).
- `SELF_KEEPALIVE_ENABLED`: Bật/tắt backend tự gọi health ping (mặc định: `true`).
- `SELF_KEEPALIVE_INTERVAL_MS`: Chu kỳ self-ping theo mili giây (mặc định: `300000`, tức 5 phút).
- `SELF_KEEPALIVE_URL`: URL self-ping, mặc định là loopback của backend.
- `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`: thông tin Cloudinary dùng để ký upload ảnh và mô hình GLB. API Secret chỉ đặt ở backend, không đưa vào frontend.
- `DEMO_LOGIN_ENABLED`: bật nút vào CMS không cần tài khoản trong giai đoạn demo (mặc định `true`); token demo chỉ có quyền đọc và hết hạn sau 30 phút.

---

## Hướng dẫn cài đặt & Khởi chạy

### 1. Build ứng dụng
```bash
mvn clean package -DskipTests
```

### 2. Khởi chạy ứng dụng
```bash
# Cách 1: Chạy trực tiếp từ file JAR
java -jar target/viettoria-be-1.0.0.jar

# Cách 2: Chạy thông qua Maven plugin
mvn spring-boot:run
```

Server sẽ khởi chạy tại: **`http://localhost:8081`**

---

## Hệ thống REST API

### Swagger / OpenAPI
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- Các endpoint quản trị được đánh dấu Bearer JWT trong Swagger. Đăng nhập qua `/api/admin/auth/login`, sau đó dán token vào nút **Authorize** để gọi các API cần đăng nhập.

### 1. Kiểm tra trạng thái máy chủ
- `GET /api/health`
- `GET /api/health/ping` — endpoint công khai cho cron bên ngoài; kiểm tra backend và chạy `SELECT 1` trên database. Trả về HTTP 200 khi cả hai hoạt động, HTTP 503 khi database không kết nối được.

Ví dụ cấu hình cron bên thứ ba: gọi `GET https://<domain-backend>/api/health/ping` mỗi 5 phút, không cần token. Không bật cache cho endpoint này.

Backend cũng tự gọi endpoint nội bộ mỗi 5 phút. Self-ping giữ database được truy vấn khi tiến trình còn chạy; nó không thể tự đánh thức tiến trình đã bị nền tảng host tạm dừng, vì vậy cron bên ngoài vẫn cần thiết nếu host có chế độ sleep.

### 2. Dành cho công chúng (Public)
- `GET /api/public/featured`: Dữ liệu tiêu điểm (Hero Product, tuyển tập, bài viết mới)
- `GET /api/public/collections`: Danh sách bộ sưu tập di sản theo thời kỳ
- `GET /api/public/collections/{slug}`: Chi tiết bộ sưu tập kèm danh sách mô hình
- `GET /api/public/products?collectionId={id}&search={keyword}`: Danh mục mô hình 3D
- `GET /api/public/products/{slugOrCode}`: Chi tiết mô hình kèm Story Blocks và Audio Guide
- `GET /api/public/qr-resolve?code={code}`: Phân giải mã tem QR dưới đế mô hình
- `GET /api/public/blogs`: Danh sách bài viết khảo cứu văn hóa
- `GET /api/public/blogs/{slug}`: Đọc bài viết nghiên cứu

### 3. Dành cho Quản trị viên (CMS Admin)
*Yêu cầu Header:* `Authorization: Bearer <token>`
- `POST /api/admin/auth/login`: Đăng nhập cấp JWT. Tài khoản khởi tạo lấy từ `ADMIN_USERNAME` và `ADMIN_PASSWORD`.
- `POST /api/admin/auth/demo`: Cấp JWT demo không cần thông tin đăng nhập; username trong phiên dùng giá trị `ADMIN_USERNAME`, nhưng role vẫn là `DEMO`, chỉ cho phép GET và không thể tạo, sửa, xóa hoặc tải tệp lên. Mật khẩu quản trị không được gửi xuống FE.
- `GET /api/admin/dashboard`: Bảng thống kê số liệu di sản
- `CRUD /api/admin/products`: Quản lý thông số kỹ thuật mô hình
- `CRUD /api/admin/products/{productId}/stories`: Quản lý các khối Story Blocks
- `PUT /api/admin/products/{productId}/stories/reorder`: Đổi thứ tự hiển thị của các khối câu chuyện
- `CRUD /api/admin/collections`: Quản lý danh mục thời kỳ lịch sử
- `CRUD /api/admin/blogs`: Quản lý bài viết góc văn hóa
- `GET /api/admin/media`: Kho lưu trữ hình ảnh khảo cứu
- `POST /api/admin/media/upload-signature`: Cấp chữ ký upload có thời hạn cho quản trị viên; chỉ ký thư mục ảnh hoặc mô hình GLB đã định sẵn.
- `POST /api/admin/media`: Lưu URL Cloudinary và thông tin tài sản vào database sau khi tải lên thành công.
- `POST /api/admin/media/fallback-upload`: Tải file multipart vào backend khi Cloudinary chưa được cấu hình hoặc upload thất bại.
- **H2 Web Console**: `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:file:./data/viettoria_db`)

## Deploy BE lên Render

Repo có `Dockerfile` và `render.yaml` để tạo Web Service bằng Render Blueprint. Khi Render hỏi các biến `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`, `CORS_ALLOWED_ORIGINS` và ba biến Cloudinary, nhập các giá trị production trong Dashboard; không commit chúng vào Git. Blueprint tự tạo `JWT_SECRET` ngẫu nhiên. CORS cần chứa domain production của Vercel, ví dụ `https://<project>.vercel.app`. Fallback lưu file tại `APP_UPLOAD_DIR` trên máy chủ; với Render cần gắn persistent disk vào thư mục này nếu muốn file backend còn tồn tại qua lần redeploy/restart.

Nếu dùng lại database đã có tài khoản admin, seed chỉ tạo tài khoản khi bảng admin đang trống; đặt biến mới không đổi mật khẩu của tài khoản đã tồn tại. Hãy đổi mật khẩu admin hiện có trước khi public service.
