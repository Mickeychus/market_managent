package com.store.quanlysieuthi.Model; // Gói Model viết hoa

// Import validation annotations
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column; // Import Column

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Column(unique = true, nullable = false) // Đảm bảo username là duy nhất trong CSDL
    private String username;

    // Password chỉ nên bắt buộc khi tạo mới. Khi sửa, có thể để trống nếu không muốn đổi.
    // Thêm validation phức tạp hơn (độ dài, ký tự đặc biệt) nếu cần.
    @Column(nullable = false) // Password trong DB không được null
    private String password; // Sẽ được mã hóa

    @NotBlank(message = "Vai trò không được để trống")
    @Column(nullable = false)
    private String role; // Ví dụ: "ROLE_ADMIN", "ROLE_USER"

    // Constructor rỗng
    public User() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}