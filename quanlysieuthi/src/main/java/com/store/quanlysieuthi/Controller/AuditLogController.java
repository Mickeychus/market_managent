package com.store.quanlysieuthi.Controller;

import com.store.quanlysieuthi.Model.AuditLog;
import com.store.quanlysieuthi.Service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
// Import PreAuthorize nếu muốn dùng kiểm tra quyền ở đây
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    // Chỉ Admin mới được truy cập URL này (đã cấu hình trong SecurityConfig)
    @GetMapping("/auditlog")
    // @PreAuthorize("hasRole('ADMIN')") // Cách khác để bảo mật
    public String showAuditLog(Model model) {
        // Lấy tất cả log, sắp xếp theo thời gian mới nhất trước
        List<AuditLog> logs = auditLogService.getAllLogs();

        // Gửi danh sách log ra view
        model.addAttribute("auditLogs", logs);

        // Trả về tên file HTML
        return "audit-log"; // Trả về templates/audit-log.html
    }
}