package com.store.quanlysieuthi.Repository; // Gói Repository viết hoa

import com.store.quanlysieuthi.Model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort; // Import Sort
import java.util.List; // Import List

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Tự động có save(), findAll(),...

    // Thêm hàm để lấy log sắp xếp theo thời gian mới nhất
    List<AuditLog> findAll(Sort sort);
}