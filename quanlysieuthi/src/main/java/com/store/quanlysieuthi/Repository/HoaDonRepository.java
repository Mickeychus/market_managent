package com.store.quanlysieuthi.Repository; // Đảm bảo tên gói đúng (viết hoa)

import com.store.quanlysieuthi.Model.HoaDon; // Import lớp HoaDon từ gói Model
import org.springframework.data.jpa.repository.JpaRepository;
// Có thể thêm import org.springframework.data.domain.Sort; nếu muốn dùng sắp xếp

import java.util.List; // Import List nếu muốn thêm hàm sắp xếp

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

    // Spring Data JPA đã tự động cung cấp các hàm cơ bản:
    // save(), findById(), findAll(), deleteById(), existsById(), ...

    // Ví dụ về hàm tìm kiếm và sắp xếp có thể thêm sau này:
    // List<HoaDon> findAllByOrderByNgayTaoDesc(); // Lấy tất cả và sắp xếp theo ngày tạo giảm dần
    // List<HoaDon> findByTenKhachHangContainingIgnoreCase(String keyword); // Tìm theo tên khách hàng
}