# Course-Service: Role-Based Access Control (RBAC) at Resource Server

## 1. Giới thiệu
Dự án Course-Service minh họa việc ứng dụng **Method Level Security** trong Spring Security 6 / Spring Boot 3 để phân quyền trực tiếp dựa trên Header `X-User-Role` và `X-User-Name` do API Gateway truyền xuống, không cần nhúng thư viện giải mã hoặc xác thực lại JWT tại Resource Server.

## 2. Công nghệ sử dụng
- Java 17 / 21
- Spring Boot 3.4.3
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Lombok
- In-memory Static List (không dùng Database)

## 3. Kiến trúc xác thực & phân quyền
- **Gateway**: Xác thực JWT token từ client, trích xuất claim `role` và chuyển tiếp sang Resource Server qua Header:
  - `X-User-Role`: Ví dụ `STUDENT` hoặc `INSTRUCTOR`
  - `X-User-Name`: Tên người dùng (tùy chọn)
- **Course-Service (Resource Server)**:
  - `HeaderAuthenticationFilter`: Đọc Header `X-User-Role` và thiết lập `Authentication` vào `SecurityContextHolder`.
  - `@EnableMethodSecurity`: Bật bảo mật mức method với `@PreAuthorize`.
  - Không cần dependency `jjwt` hoặc `spring-boot-starter-oauth2-resource-server`.

## 4. Danh sách API

| Phương thức | Endpoint | Yêu cầu phân quyền | Mô tả |
|-------------|----------|-------------------|-------|
| `GET` | `/api/courses` | `@PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")` | Xem danh sách các khóa học |
| `POST` | `/api/courses` | `@PreAuthorize("hasAuthority('INSTRUCTOR')")` | Tạo khóa học mới |

### Request Body cho `POST /api/courses`:
```json
{
  "title": "Spring Cloud & Microservices",
  "description": "Building scalable microservices architecture",
  "instructor": "Instructor Alpha"
}
```

## 5. Hướng dẫn kiểm thử

### Kiểm thử với Postman (hoặc curl):
1. **Xem danh sách khóa học (STUDENT hoặc INSTRUCTOR):**
   ```bash
   curl -X GET http://localhost:8083/api/courses \
     -H "X-User-Role: STUDENT" \
     -H "X-User-Name: student1"
   ```
   **Kết quả:** `200 OK` và danh sách JSON các khóa học.

2. **Tạo khóa học bằng tài khoản STUDENT:**
   ```bash
   curl -X POST http://localhost:8083/api/courses \
     -H "X-User-Role: STUDENT" \
     -H "X-User-Name: student1" \
     -H "Content-Type: application/json" \
     -d '{"title":"Docker Fundamentals","description":"Learn Docker","instructor":"John"}'
   ```
   **Kết quả:** `403 Forbidden` (đúng như yêu cầu bài toán).

3. **Tạo khóa học bằng tài khoản INSTRUCTOR:**
   ```bash
   curl -X POST http://localhost:8083/api/courses \
     -H "X-User-Role: INSTRUCTOR" \
     -H "X-User-Name: instructor1" \
     -H "Content-Type: application/json" \
     -d '{"title":"Docker Fundamentals","description":"Learn Docker","instructor":"Instructor Alpha"}'
   ```
   **Kết quả:** `201 Created` và thông tin khóa học vừa tạo.
