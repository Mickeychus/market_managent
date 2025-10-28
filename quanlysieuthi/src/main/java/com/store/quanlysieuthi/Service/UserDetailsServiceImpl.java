package com.store.quanlysieuthi.Service; // Gói Service viết hoa

import com.store.quanlysieuthi.Model.User;
import com.store.quanlysieuthi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import thêm
import org.springframework.stereotype.Service; // Import Service

import java.util.Collections; // Import Collections

@Service // Đánh dấu đây là một Service Bean
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user trong CSDL bằng username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        // Chuyển đổi thông tin User (từ CSDL) thành đối tượng UserDetails mà Spring Security hiểu
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                // Chuyển đổi role (String) thành danh sách quyền (GrantedAuthority)
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}