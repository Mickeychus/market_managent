package com.store.quanlysieuthi.Service;

import com.store.quanlysieuthi.Model.User;
import com.store.quanlysieuthi.Repository.UserRepository;
import com.store.quanlysieuthi.Service.AuditLogService; // **THÊM IMPORT**
import org.springframework.beans.factory.annotation.Autowired; // **ĐẢM BẢO CÓ IMPORT NÀY**
import org.springframework.security.core.Authentication;   // **THÊM IMPORT**
import org.springframework.security.core.context.SecurityContextHolder; // **THÊM IMPORT**
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired // **THÊM**: Tiêm AuditLogService
    private AuditLogService auditLogService;

    // --- Lấy tất cả user ---
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // --- Tìm user theo ID ---
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    // --- Tìm user theo username ---
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // --- Lưu User (Tạo mới hoặc Cập nhật) với Audit Log ---
    @Transactional
    public User saveUser(User user, String rawPassword) throws Exception {
        boolean isNew = (user.getId() == null);
        String oldUsername = null;
        String oldRole = null; // Lưu vai trò cũ
        boolean passwordChanged = false; // Đánh dấu mật khẩu thay đổi

        // Kiểm tra trùng username
        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent() && (isNew || !existingUserByUsername.get().getId().equals(user.getId()))) {
            throw new Exception("Tên đăng nhập '" + user.getUsername() + "' đã tồn tại!");
        }

        User userToSave = user;
        if (!isNew) {
            User oldUser = userRepository.findById(user.getId())
                                    .orElseThrow(() -> new Exception("Không tìm thấy người dùng để cập nhật."));
            oldUsername = oldUser.getUsername();
            oldRole = oldUser.getRole(); // Lấy vai trò cũ
            if (rawPassword != null && !rawPassword.isBlank()) {
                userToSave.setPassword(passwordEncoder.encode(rawPassword));
                passwordChanged = true; // Đánh dấu đã đổi pass
            } else {
                userToSave.setPassword(oldUser.getPassword()); // Giữ pass cũ
            }
        } else {
             if (rawPassword == null || rawPassword.isBlank()) {
                throw new Exception("Mật khẩu là bắt buộc khi tạo người dùng mới.");
            }
            userToSave.setPassword(passwordEncoder.encode(rawPassword));
            passwordChanged = true; // Mật khẩu được đặt khi tạo mới
        }

        if (userToSave.getRole() == null || !userToSave.getRole().startsWith("ROLE_")) {
             throw new Exception("Vai trò không hợp lệ. Phải bắt đầu bằng ROLE_.");
        }

        // Lưu user
        User savedUser = userRepository.save(userToSave);

        // **GHI LOG**: Ghi log sau khi lưu
        String action = isNew ? "CREATE_USER" : "UPDATE_USER";
        StringBuilder detailsBuilder = new StringBuilder();
        detailsBuilder.append(String.format("ID: %d, Username: %s, Role: %s",
                                       savedUser.getId(), savedUser.getUsername(), savedUser.getRole()));
        if (!isNew) { // Chỉ thêm chi tiết thay đổi khi sửa
             if(!savedUser.getRole().equals(oldRole)){
                 detailsBuilder.append(String.format(" (Role cũ: %s)", oldRole));
             }
             if(passwordChanged){
                 detailsBuilder.append(" (Mật khẩu đã thay đổi)");
             }
             // Username hiện không cho đổi
        }
        auditLogService.logAction(action, detailsBuilder.toString());

        return savedUser;
    }

    // --- Xóa User với Audit Log ---
    @Transactional
    public void deleteUser(Long id) throws Exception {
        String currentUsername = getCurrentUsername();

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng để xóa."));

        if (userToDelete.getUsername().equals(currentUsername)) {
            // **GHI LOG**: Ghi log cố gắng tự xóa
            auditLogService.logAction("DELETE_USER_SELF_ATTEMPT", "User: " + currentUsername + " đã cố tự xóa tài khoản.");
            throw new Exception("Bạn không thể tự xóa tài khoản của chính mình!");
        }

        // Chuẩn bị thông tin log trước khi xóa
        String details = String.format("ID: %d, Username: %s, Role: %s",
                                       userToDelete.getId(), userToDelete.getUsername(), userToDelete.getRole());

        userRepository.deleteById(id);

        // **GHI LOG**: Ghi log sau khi xóa thành công
        auditLogService.logAction("DELETE_USER", details);
         System.out.println(">>> Đã xóa user " + userToDelete.getUsername());
    }

     // Hàm lấy username hiện tại
     private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return "SYSTEM";
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        if (principal instanceof String) return (String) principal;
        return "UNKNOWN";
    }
}