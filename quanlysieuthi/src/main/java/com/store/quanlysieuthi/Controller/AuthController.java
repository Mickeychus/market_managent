package com.store.quanlysieuthi.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Khi người dùng truy cập "/login" bằng GET, hiển thị trang login.html
    @GetMapping("/login")
    public String loginPage() {
        // Kiểm tra xem người dùng đã đăng nhập chưa, nếu rồi thì chuyển hướng
        // (Sẽ thêm logic này sau nếu cần, tạm thời cứ trả về trang login)
        return "login"; // Trả về tên file templates/login.html
    }

    // Có thể thêm trang chủ ở đây nếu muốn
    @GetMapping("/")
    public String homePage() {
        // Tự động chuyển đến trang bán hàng nếu đã đăng nhập
        return "redirect:/banhang";
    }
}