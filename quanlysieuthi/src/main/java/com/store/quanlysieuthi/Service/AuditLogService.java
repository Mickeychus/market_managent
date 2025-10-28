package com.store.quanlysieuthi.Service;

import com.store.quanlysieuthi.Model.AuditLog;
import com.store.quanlysieuthi.Repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort; // Import Sort
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Hàm để ghi log
    // @Transactional(propagation = Propagation.REQUIRES_NEW) // Chạy trong giao dịch mới
    // Bỏ @Transactional ở đây để tránh lỗi nếu hành động chính bị rollback
    public void logAction(String action, String details) {
        String username = getCurrentUsername(); // Lấy username người dùng hiện tại
        AuditLog logEntry = new AuditLog(username, action, details);
        try {
             auditLogRepository.save(logEntry);
        } catch (Exception e) {
            // Xử lý lỗi nếu không ghi được log (ví dụ: ghi vào file log thay thế)
             System.err.println("!!! LỖI GHI AUDIT LOG: " + e.getMessage());
             e.printStackTrace();
        }
    }

    // Hàm lấy tất cả log (sắp xếp mới nhất trước)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    // Hàm tiện ích lấy username người dùng đang đăng nhập
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM"; // Hoặc "ANONYMOUS" nếu hành động không cần đăng nhập
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
             return (String) principal; // Đôi khi principal là String (ví dụ: remember-me)
        } else {
             return "UNKNOWN"; // Trường hợp không xác định được
        }
    }
}