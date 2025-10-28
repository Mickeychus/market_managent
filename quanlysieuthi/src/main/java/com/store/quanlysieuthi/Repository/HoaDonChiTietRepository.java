package com.store.quanlysieuthi.Repository;

import com.store.quanlysieuthi.Model.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Long> {
    // Có thể thêm các hàm tìm kiếm chi tiết sau (ví dụ: theo hóa đơn ID)
}