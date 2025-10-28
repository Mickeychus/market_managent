package com.store.quanlysieuthi.Model; // Gói Model viết hoa

import jakarta.persistence.*; // Import *
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log") // Tên bảng lưu lịch sử
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp; // Thời gian xảy ra hành động

    @Column(nullable = false)
    private String username; // Tên người dùng thực hiện

    @Column(nullable = false)
    private String action; // Mô tả hành động (e.g., "CREATE_PRODUCT", "DELETE_HOADON", "UPDATE_USER")

    @Lob // Dùng cho kiểu dữ liệu lớn (TEXT/CLOB trong DB)
    @Column(columnDefinition = "TEXT") // Chỉ định kiểu TEXT cho MySQL
    private String details; // Chi tiết về đối tượng bị tác động (e.g., ID sản phẩm, tên khách hàng...)

    // Constructor rỗng
    public AuditLog() {
        this.timestamp = LocalDateTime.now(); // Tự đặt thời gian hiện tại
    }

    // Constructor tiện ích
    public AuditLog(String username, String action, String details) {
        this(); // Gọi constructor rỗng để đặt timestamp
        this.username = username;
        this.action = action;
        this.details = details;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}