package com.store.quanlysieuthi.Service;

import com.store.quanlysieuthi.Model.User;
import com.store.quanlysieuthi.Repository.UserRepository;
import com.store.quanlysieuthi.Service.AuditLogService; // Phải import AuditLogService
import com.store.quanlysieuthi.Service.UserService; // Import lớp đang test

import org.junit.jupiter.api.BeforeEach; // Import cho JUnit 5
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; // Tiêm đối tượng cần test
import org.mockito.Mock; // Tạo đối tượng giả
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser; // Giả lập user đăng nhập

import java.util.Optional;

// Import các hàm static của Mockito và Assertions
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Kích hoạt Mockito
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService; // Giả lập dịch vụ log

    @InjectMocks
    private UserService userService;

    private User adminUser;

    @BeforeEach // Hàm này chạy trước mỗi @Test
    void setUp() {
        // Chuẩn bị một user mẫu
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedPassword123"); // Mật khẩu đã mã hóa
        adminUser.setRole("ROLE_ADMIN");
    }

    // --- Test chức năng saveUser (Tạo mới) ---
    
    @Test
    void testSaveUser_Create_Success() throws Exception {
        // 1. Chuẩn bị
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setRole("ROLE_USER");
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword456";

        // Định nghĩa hành vi của mock:
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        doNothing().when(auditLogService).logAction(anyString(), anyString()); // Bỏ qua việc ghi log

        // 2. Thực thi
        User savedUser = userService.saveUser(newUser, rawPassword);

        // 3. Kiểm chứng
        assertNotNull(savedUser); 
        assertEquals("newUser", savedUser.getUsername()); 
        assertEquals(encodedPassword, savedUser.getPassword()); 
        verify(userRepository, times(1)).save(newUser);
        verify(auditLogService, times(1)).logAction(eq("CREATE_USER"), anyString());
    }

    @Test
    void testSaveUser_Create_UsernameExists() {
        // 1. Chuẩn bị
        User newUser = new User();
        newUser.setUsername("admin");
        newUser.setRole("ROLE_USER");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // 2. Thực thi & 3. Kiểm chứng
        Exception exception = assertThrows(Exception.class, () -> {
            userService.saveUser(newUser, "password123");
        });

        assertEquals("Tên đăng nhập 'admin' đã tồn tại!", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testSaveUser_Create_PasswordBlank() {
        // 1. Chuẩn bị
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setRole("ROLE_USER");

        // 2. Thực thi & 3. Kiểm chứng
        Exception exception = assertThrows(Exception.class, () -> {
            userService.saveUser(newUser, ""); // Mật khẩu rỗng
        });

        assertEquals("Mật khẩu là bắt buộc khi tạo người dùng mới.", exception.getMessage());
    }

    // --- Test chức năng saveUser (Cập nhật) ---

    @Test
    void testSaveUser_Update_PasswordNotChanged() throws Exception {
        // 1. Chuẩn bị
        adminUser.setRole("ROLE_SUPER_ADMIN"); 
        
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser)); 
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        doNothing().when(auditLogService).logAction(anyString(), anyString()); // Bỏ qua log

        // 2. Thực thi
        User updatedUser = userService.saveUser(adminUser, null); 

        // 3. Kiểm chứng
        assertNotNull(updatedUser);
        assertEquals("ROLE_SUPER_ADMIN", updatedUser.getRole()); // Vai trò được cập nhật
        assertEquals("encodedPassword123", updatedUser.getPassword()); // Mật khẩu được giữ nguyên
        verify(passwordEncoder, times(0)).encode(anyString()); // Đảm bảo hàm encode *không* được gọi
        verify(auditLogService, times(1)).logAction(eq("UPDATE_USER"), anyString());
    }

    // --- Test chức năng deleteUser ---

    @Test
    @WithMockUser(username = "admin") // Giả lập người dùng "admin" đang đăng nhập
    void testDeleteUser_SelfDelete_ShouldFail() {
        // 1. Chuẩn bị
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        
        // 2. Thực thi & 3. Kiểm chứng
        Exception exception = assertThrows(Exception.class, () -> {
            userService.deleteUser(1L); // Cố gắng xóa ID 1 (chính là "admin")
        });

        assertEquals("Bạn không thể tự xóa tài khoản của chính mình!", exception.getMessage());
        verify(userRepository, times(0)).deleteById(anyLong());
        verify(auditLogService, times(1)).logAction(eq("DELETE_USER_SELF_ATTEMPT"), anyString());
    }

    @Test
    @WithMockUser(username = "admin") // Giả lập "admin" đang đăng nhập
    void testDeleteUser_Success() throws Exception {
        // 1. Chuẩn bị
        User userToDelete = new User();
        userToDelete.setId(2L);
        userToDelete.setUsername("user");
        userToDelete.setRole("ROLE_USER");
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToDelete));
        doNothing().when(userRepository).deleteById(2L);
        doNothing().when(auditLogService).logAction(anyString(), anyString()); // Bỏ qua log

        // 2. Thực thi
        userService.deleteUser(2L); // Admin xóa user (ID 2)

        // 3. Kiểm chứng
        verify(userRepository, times(1)).deleteById(2L);
        verify(auditLogService, times(1)).logAction(eq("DELETE_USER"), anyString());
    }
}