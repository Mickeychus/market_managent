package com.store.quanlysieuthi.Controller;

import com.store.quanlysieuthi.Model.User;
import com.store.quanlysieuthi.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // Có thể dùng thay SecurityConfig
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; // Import *
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users") // Tiền tố chung cho tất cả các URL trong Controller này
// @PreAuthorize("hasRole('ADMIN')") // Áp dụng quyền ADMIN cho cả Controller (cách khác)
public class UserController {

    @Autowired
    private UserService userService;

    // --- Hiển thị danh sách Users ---
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user-list"; // Trả về templates/user-list.html
    }

    // --- Hiển thị Form Thêm User ---
    @GetMapping("/them")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User()); // Đối tượng User rỗng cho form
        model.addAttribute("pageTitle", "Thêm Người Dùng Mới");
        model.addAttribute("isEditMode", false); // Đánh dấu là form thêm mới
        return "user-form"; // Trả về templates/user-form.html
    }

    // --- Hiển thị Form Sửa User ---
    @GetMapping("/sua/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get()); // Gửi User hiện tại ra form
            model.addAttribute("pageTitle", "Sửa Người Dùng (ID: " + id + ")");
            model.addAttribute("isEditMode", true); // Đánh dấu là form sửa
            return "user-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng với ID: " + id);
            return "redirect:/users";
        }
    }

    // --- Xử lý Lưu User (Thêm và Sửa) ---
    @PostMapping("/luu")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           @RequestParam(value = "rawPassword", required = false) String rawPassword, // Nhận mật khẩu mới (không bắt buộc khi sửa)
                           Model model,
                           RedirectAttributes redirectAttributes) {

        // Kiểm tra validation cơ bản (NotBlank, Size cho username/role)
        if (bindingResult.hasErrors()) {
            System.out.println("Lỗi validation cơ bản: " + bindingResult.getAllErrors());
            model.addAttribute("pageTitle", (user.getId() == null) ? "Thêm Người Dùng Mới (Lỗi)" : "Sửa Người Dùng (Lỗi)");
            model.addAttribute("isEditMode", user.getId() != null); // Gửi lại trạng thái form
            return "user-form";
        }

        // Gọi Service để xử lý lưu và validation phức tạp hơn (trùng username, password)
        try {
            userService.saveUser(user, rawPassword);
            redirectAttributes.addFlashAttribute("successMessage",
                    (user.getId() == null || user.getId() == 0) ? "Đã thêm người dùng thành công!" : "Đã cập nhật người dùng thành công!");
            return "redirect:/users";
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu người dùng: " + e.getMessage());
            // bindingResult.rejectValue("username", "error.user", e.getMessage()); // Cách thêm lỗi vào BindingResult
            model.addAttribute("errorMessage", e.getMessage()); // Hoặc gửi lỗi chung
            model.addAttribute("pageTitle", (user.getId() == null) ? "Thêm Người Dùng Mới (Lỗi)" : "Sửa Người Dùng (Lỗi)");
            model.addAttribute("isEditMode", user.getId() != null);
            // Gửi lại user để giữ dữ liệu form, nhưng xóa password đi
            user.setPassword(null); // Không gửi lại password đã mã hóa hoặc password lỗi
            model.addAttribute("user", user);
            return "user-form";
        }
    }

    // --- Xử lý Xóa User ---
    @GetMapping("/xoa/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa người dùng thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa người dùng: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}