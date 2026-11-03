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
- `DATABASE_URL`: URI kết nối PostgreSQL (Neon / Supabase / RDS).
- `SPRING_DATASOURCE_URL`: JDBC URL kết nối cơ sở dữ liệu (`jdbc:postgresql://...`).
- `SPRING_DATASOURCE_USERNAME`: Tên người dùng database.
- `SPRING_DATASOURCE_PASSWORD`: Mật khẩu database.
- `SERVER_PORT`: Cổng máy chủ (mặc định: `5000`).
- `JWT_SECRET`: Khóa bí mật ký token HMAC-SHA256.

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

Server sẽ khởi chạy tại: **`http://localhost:5000`**

---

## Hệ thống REST API

### 1. Kiểm tra trạng thái máy chủ
- `GET /api/health`

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
- `POST /api/admin/auth/login`: Đăng nhập cấp JWT (Tài khoản: `admin` / `viettoria@2026`)
- `GET /api/admin/dashboard`: Bảng thống kê số liệu di sản
- `CRUD /api/admin/products`: Quản lý thông số kỹ thuật mô hình
- `CRUD /api/admin/products/{productId}/stories`: Quản lý các khối Story Blocks
- `PUT /api/admin/products/{productId}/stories/reorder`: Đổi thứ tự hiển thị của các khối câu chuyện
- `CRUD /api/admin/collections`: Quản lý danh mục thời kỳ lịch sử
- `CRUD /api/admin/blogs`: Quản lý bài viết góc văn hóa
- `GET /api/admin/media`: Kho lưu trữ hình ảnh khảo cứu
- **H2 Web Console**: `http://localhost:5000/h2-console` (JDBC URL: `jdbc:h2:file:./data/viettoria_db`)
