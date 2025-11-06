# Quản Lý Bán Hàng Siêu Thị

## Sinh viên thực hiện
- **Chu Việt Long** - 22010107

---

## 📌 Giới thiệu
Đây là bài tập lớn môn **Lập trình Hướng Đối Tượng (OOP)** của nhóm sinh viên CNTT, Đại học Phenikaa.

Ứng dụng được xây dựng nhằm hỗ trợ siêu thị trong việc quản lý sản phẩm, bán hàng, tài khoản người dùng và thống kê doanh thu.

Hệ thống được phát triển bằng **Spring Boot** sử dụng kiến trúc phân lớp (MVC) với các công nghệ:
- **Backend:** Java, Spring Boot, Spring Security
- **Frontend:** Thymeleaf, HTML, CSS
- **Dữ liệu:** Spring Data JPA, Cloud MySQL (Aiven)

---
## UML Sequence Diagram
<p align="center">
  <i>*Lưu ý: Các sơ đồ cần được vẽ lại để phản ánh kiến trúc Spring Boot (Controller, Service, Repository).*</i>
  <br>
  
  <br>
  <img src="UML%20Sequence%20Diagram.png" alt="Sequence Diagram" width="600">
</p>

## UML Class Diagram
<p align="center">
  <i>*Lưu ý: Sơ đồ lớp cần được vẽ lại để bao gồm các Entity, Repository, Service, và Controller.*</i>
  <br>
  
  <br>
  <img src="UML%20Class%20Diagram.jpg" alt="Class Diagram" width="600">
</p>

---

## Chức năng chính
- **Bảo mật & Phân quyền (Spring Security)**
  - Đăng nhập (trang tùy chỉnh) và Đăng xuất.
  - Phân quyền dựa trên vai trò (`ROLE_ADMIN`, `ROLE_USER`).
  - Giao diện (Menu, Nút) tự động ẩn/hiện theo quyền.
  - Mã hóa mật khẩu người dùng (BCrypt).

- **Chức năng Quản trị (Admin)**
  - **Quản lý Sản phẩm (CRUD):** Thêm, Sửa, Xóa sản phẩm (với validation).
  - **Quản lý Người dùng (CRUD):** Thêm, Sửa, Xóa tài khoản (ngăn tự xóa).
  - **Quản lý Hóa đơn:** Sửa (tên khách hàng) và Xóa (có trả lại tồn kho).
  - **Thống kê:** Xem trang thống kê tổng hợp (doanh thu, tồn kho, số lượng...).
  - **Lịch sử:** Xem nhật ký toàn bộ hành động (Audit Log) của hệ thống.

- **Chức năng Nghiệp vụ (User & Admin)**
  - **Bán hàng:** Giao diện 2 cột (Sản phẩm & Giỏ hàng).
  - **Giỏ hàng (Session):** Thêm, Sửa số lượng, Xóa khỏi giỏ.
  - **Quản lý Tồn kho:** Tự động trừ tồn kho khi thêm vào giỏ, trả lại tồn kho khi xóa khỏi giỏ/sửa giảm.
  - **Lưu Hóa đơn:** Chuyển giỏ hàng (Session) thành hóa đơn chính thức (lưu vào CSDL).
  - **Xem Hóa đơn:** Xem danh sách và chi tiết các hóa đơn đã lưu.

---

## Cấu trúc Dự án (Spring Boot)
Dự án được xây dựng theo kiến trúc phân lớp `Controller` - `Service` - `Repository`.

### 1. Model (Gói `Model`)
Định nghĩa các đối tượng Entity (ánh xạ CSDL) và POJO (đối tượng dữ liệu tạm).
- **`User.java`**: (Entity) Lưu thông tin tài khoản (username, password, role).
- **`SanPham.java`**: (Entity) Lưu thông tin sản phẩm (tên, giá nhập, giá bán, tồn kho...).
- **`HoaDon.java`**: (Entity) Thông tin chính của hóa đơn (tên khách, ngày tạo, tổng tiền).
- **`HoaDonChiTiet.java`**: (Entity) Các mặt hàng chi tiết trong một hóa đơn (liên kết Many-to-One với `HoaDon`).
- **`AuditLog.java`**: (Entity) Lưu nhật ký hành động (ai, làm gì, khi nào, chi tiết).
- **`CartItem.java`**: (POJO) Đối tượng biểu diễn một mặt hàng trong giỏ hàng (Session), không lưu vào CSDL.

### 2. Repository (Gói `Repository`)
Các `interface` kế thừa `JpaRepository` để Spring Data JPA tự động xử lý các thao tác CSDL.
- **`UserRepository`**: CRUD cho `User`, có hàm `findByUsername`.
- **`SanPhamRepository`**: CRUD cho `SanPham`, có hàm `findByTenSP`.
- **`HoaDonRepository`**: CRUD cho `HoaDon`.
- **`HoaDonChiTietRepository`**: CRUD cho `HoaDonChiTiet`.
- **`AuditLogRepository`**: CRUD cho `AuditLog`.

### 3. Service (Gói `Service`)
Nơi xử lý logic nghiệp vụ (business logic) phức tạp.
- **`UserService`**: Xử lý logic Thêm/Sửa/Xóa user, kiểm tra trùng lặp, mã hóa mật khẩu, ngăn tự xóa.
- **`CartService`**: (`@SessionScope`) Quản lý giỏ hàng ảo, xử lý logic Thêm/Xóa/Sửa giỏ hàng và cập nhật tồn kho (liên kết với `SanPhamRepository`).
- **`AuditLogService`**: Cung cấp hàm `logAction` để ghi nhật ký và `getAllLogs` để lấy lịch sử.
- **`ThongKeService`**: Tính toán các số liệu thống kê cho trang Admin.
- **`UserDetailsServiceImpl`**: Lớp dịch vụ để Spring Security tìm và xác thực người dùng từ `UserRepository`.

### 4. Controller (Gói `Controller`)
Tiếp nhận yêu cầu HTTP từ trình duyệt, gọi Service/Repository, và trả về View (HTML) cho người dùng.
- **`SanPhamController`**: Xử lý CRUD cho sản phẩm (`/sanpham`).
- **`UserController`**: Xử lý CRUD cho người dùng (`/users`).
- **`BillController`**: Xử lý trang bán hàng, giỏ hàng (`/banhang`) và lưu hóa đơn.
- **`HoaDonController`**: Xử lý xem danh sách (`/hoadon`), chi tiết, sửa, xóa hóa đơn.
- **`ThongKeController`**: Hiển thị trang thống kê (`/thongke`).
- **`AuditLogController`**: Hiển thị trang lịch sử (`/auditlog`).
- **`AuthController`**: Hiển thị trang đăng nhập tùy chỉnh (`/login`).

### 5. Config (Gói `Config`)
Cấu hình hệ thống.
- **`SecurityConfig.java`**: Cấu hình Spring Security (mã hóa, phân quyền URL, trang đăng nhập/logout).

### 6. View (Thư mục `src/main/resources/templates`)
Các tệp HTML sử dụng **Thymeleaf** để hiển thị giao diện.
- **`login.html`**: Trang đăng nhập tùy chỉnh.
- **`_header.html`**: (Fragment) Thanh điều hướng chung, hiển thị menu theo quyền.
- **`banhang.html`**: Trang bán hàng chính (danh sách SP và giỏ hàng).
- **`sanpham-list.html`**, **`sanpham-form.html`**: Trang CRUD sản phẩm.
- **`user-list.html`**, **`user-form.html`**: Trang CRUD người dùng.
- **`hoadon-list.html`**, **`hoadon-detail.html`**, **`hoadon-form.html`**: Trang CRUD hóa đơn.
- **`thongke.html`**: Trang hiển thị thống kê.
- **`audit-log.html`**: Trang hiển thị lịch sử.

---

## Mã nguồn CRUD (Controller & Service)
Logic CRUD trong Spring Boot chủ yếu được xử lý bởi `JpaRepository`. Controller và Service gọi các hàm này và thêm logic nghiệp vụ (validation, ghi log).

### 4.1. SanPhamController (Create/Update)
Sử dụng `@Valid` để kiểm tra validation từ Entity `SanPham`.

```java
// CREATE & UPDATE
@PostMapping("/sanpham/luu")
public String luuSanPham(@Valid @ModelAttribute("sanPhamMoi") SanPham sanPham,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
    if (bindingResult.hasErrors()) {
        model.addAttribute("pageTitle", (sanPham.getId() == null) ? "Thêm Sản phẩm Mới (Lỗi)" : "Sửa Sản phẩm (Lỗi)");
        return "sanpham-form"; // Quay lại form nếu có lỗi
    }
    try {
        boolean isNew = (sanPham.getId() == null || sanPham.getId() == 0);
        SanPham savedSanPham = sanPhamRepo.save(sanPham); // JPA xử lý save
        
        // Ghi log
        String action = isNew ? "CREATE_PRODUCT" : "UPDATE_PRODUCT";
        String details = String.format("ID: %d, Tên: %s, SL: %d",
                                        savedSanPham.getId(), savedSanPham.getTenSP(), savedSanPham.getSoLuong());
        auditLogService.logAction(action, details);
        
        redirectAttributes.addFlashAttribute("successMessage", isNew ? "Đã thêm!" : "Đã cập nhật!");
    } catch (Exception e) {
        auditLogService.logAction("SAVE_PRODUCT_ERROR", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu sản phẩm.");
    }
    return "redirect:/sanpham";
}
4.2. HoaDonController (Delete)
Sử dụng @Transactional để đảm bảo việc xóa và trả tồn kho diễn ra an toàn.

Java

// DELETE
@GetMapping("/hoadon/xoa/{id}")
@Transactional // Đảm bảo tất cả thành công, hoặc rollback
public String xoaHoaDon(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    try {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isPresent()) {
            HoaDon hoaDon = optionalHoaDon.get();
            String details = String.format("ID Hóa đơn: %d, Khách hàng: %s", hoaDon.getId(), hoaDon.getTenKhachHang());

            // 1. Trả lại tồn kho
            for (HoaDonChiTiet chiTiet : hoaDon.getChiTiet()) {
                List<SanPham> sanPhams = sanPhamRepo.findByTenSP(chiTiet.getTenSanPham());
                if (!sanPhams.isEmpty()) {
                    SanPham spGoc = sanPhams.get(0);
                    spGoc.setSoLuong(spGoc.getSoLuong() + chiTiet.getSoLuong());
                    sanPhamRepo.save(spGoc);
                }
            }

            // 2. Xóa hóa đơn (Cascade tự xóa chi tiết)
            hoaDonRepo.deleteById(id);
            
            // 3. Ghi log
            auditLogService.logAction("DELETE_HOADON", details + " (Đã trả tồn kho)");
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa hóa đơn #" + id + " và trả lại tồn kho.");
        } else {
            // ... (xử lý không tìm thấy) ...
        }
    } catch (Exception e) {
        // ... (xử lý lỗi) ...
    }
    return "redirect:/hoadon";
}
4.3. UserService (Delete)
Xử lý logic nghiệp vụ (ngăn tự xóa) trong Service trước khi gọi Repository.

Java

// DELETE (trong UserService.java)
@Transactional
public void deleteUser(Long id) throws Exception {
    String currentUsername = getCurrentUsername(); // Lấy user đang đăng nhập

    User userToDelete = userRepository.findById(id)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng để xóa."));

    // Logic nghiệp vụ: Ngăn tự xóa
    if (userToDelete.getUsername().equals(currentUsername)) {
        auditLogService.logAction("DELETE_USER_SELF_ATTEMPT", "User: " + currentUsername + " đã cố tự xóa.");
        throw new Exception("Bạn không thể tự xóa tài khoản của chính mình!");
    }

    String details = String.format("ID: %d, Username: %s", userToDelete.getId(), userToDelete.getUsername());
    userRepository.deleteById(id); // Gọi Repository
    auditLogService.logAction("DELETE_USER", details);
}

---

## 🚀 Hướng dẫn sử dụng
Khởi chạy: Chạy tệp QuanlysieuthiApplication.java.

Truy cập: Mở trình duyệt và đi đến http://localhost:8080/ (sẽ tự chuyển đến trang đăng nhập).

Đăng nhập:

Tài khoản Admin: admin / admin

Tài khoản User: user / user

Menu (Header):

Bán Hàng: Chức năng chính, tạo giỏ hàng và lưu hóa đơn (cho cả Admin và User).

Hóa Đơn: Xem danh sách và chi tiết các hóa đơn đã lưu (cho cả Admin và User).

Quản lý Sản phẩm: (Admin) CRUD sản phẩm.

Thống Kê: (Admin) Xem thống kê.

Quản lý Tài khoản: (Admin) CRUD tài khoản.

Lịch Sử: (Admin) Xem nhật ký hệ thống.

Xin chào, [username]! [Đăng xuất]: Hiển thị thông tin đăng nhập.

---

## 📖 Tài liệu tham khảo
(Giữ nguyên các tài liệu tham khảo của bạn, nhưng thay thế các tài liệu về XML/Swing bằng Spring Boot/JPA)

Spring.io (2024). Spring Boot Reference Documentation. URL: https://docs.spring.io/spring-boot/docs/current/reference/html/

Baeldung (2024). Spring Data JPA. URL: https://www.baeldung.com/spring-data-jpa-query

Baeldung (2024). Using Thymeleaf with Spring Security. URL: https://www.baeldung.com/thymeleaf-spring-security

VietNix(26/07/2022), Tìm hiểu mô hình MVC là gì? Ví dụ về cách sử dụng mô hình MVC.

Nguyễn Thanh Tùng (2020). Giáo trình Lập trình hướng đối tượng với Java. NXB Thông tin và Truyền thông.

Oracle (2024). The Java™ Platform, Standard Edition Documentation. URL: https://docs.oracle.com/javase/

GeeksforGeeks (2024). Introduction to Object-Oriented Programming in Java.
