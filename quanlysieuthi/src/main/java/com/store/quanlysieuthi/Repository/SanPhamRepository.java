package com.store.quanlysieuthi.Repository; // Gói Repository viết hoa

import org.springframework.data.jpa.repository.JpaRepository;
import com.store.quanlysieuthi.Model.SanPham; // Import lớp SanPham từ Model
import java.util.List; // **THÊM IMPORT NÀY**

public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    // **THÊM PHƯƠNG THỨC NÀY VÀO**
    // Spring Data JPA sẽ tự động tạo câu lệnh tìm kiếm theo tên sản phẩm
    List<SanPham> findByTenSP(String tenSP);

}