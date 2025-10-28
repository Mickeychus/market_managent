package com.store.quanlysieuthi.Repository; // Gói Repository viết hoa

import com.store.quanlysieuthi.Model.User; // Import User từ Model
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Import Optional

public interface UserRepository extends JpaRepository<User, Long> {

    // Thêm phương thức này để Spring Security có thể tìm User theo username
    Optional<User> findByUsername(String username);
}