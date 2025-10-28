package com.store.quanlysieuthi.Repository; // Gói Repository viết hoa

import com.store.quanlysieuthi.Model.Bill; // Import lớp Bill từ Model
import org.springframework.data.jpa.repository.JpaRepository;

// Interface này kế thừa JpaRepository để tự động có các hàm CRUD
public interface BillRepository extends JpaRepository<Bill, Long> {

    // Spring Data JPA sẽ tự động cung cấp các hàm save(), findAll(), findById(), deleteById(),...
    // Sau này có thể thêm các hàm tìm kiếm tùy chỉnh, ví dụ:
    // List<Bill> findByMaHoaDon(String maHoaDon); // Tìm các mặt hàng theo mã hóa đơn
}