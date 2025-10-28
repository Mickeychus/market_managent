package com.store.quanlysieuthi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.store.quanlysieuthi.Model.User; // Đảm bảo import đúng lớp User từ gói Model viết hoa
import com.store.quanlysieuthi.Repository.UserRepository; // Đảm bảo import đúng UserRepository từ gói Repository viết hoa
// --- Kết thúc thêm import ---

@SpringBootApplication
public class QuanlysieuthiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanlysieuthiApplication.class, args);
    }

    // --- Thêm Bean này để tạo người dùng admin/user khi khởi động ---
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Kiểm tra xem admin đã tồn tại chưa
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                // Mã hóa mật khẩu "admin" bằng BCrypt trước khi lưu
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setRole("ROLE_ADMIN"); // Đặt vai trò (quan trọng có ROLE_)
                userRepository.save(adminUser);
                System.out.println(">>> Đã tạo người dùng 'admin' với mật khẩu 'admin'");
            }
             // Tạo thêm user thường nếu muốn
             if (userRepository.findByUsername("user").isEmpty()) {
                User normalUser = new User();
                normalUser.setUsername("user");
                // Mã hóa mật khẩu "user"
                normalUser.setPassword(passwordEncoder.encode("user"));
                normalUser.setRole("ROLE_USER"); // Đặt vai trò (quan trọng có ROLE_)
                userRepository.save(normalUser);
                System.out.println(">>> Đã tạo người dùng 'user' với mật khẩu 'user'");
            }
        };
    }
    // --- Kết thúc thêm Bean ---
}