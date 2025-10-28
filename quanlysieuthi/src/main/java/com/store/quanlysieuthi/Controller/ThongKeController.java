package com.store.quanlysieuthi.Controller;

import com.store.quanlysieuthi.Service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // Import để kiểm tra quyền
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    // Chỉ Admin mới được truy cập URL này
    @GetMapping("/thongke")
    // @PreAuthorize("hasRole('ADMIN')") // Cách khác để kiểm tra quyền thay vì SecurityConfig
    public String trangThongKe(Model model) {
        // Lấy dữ liệu thống kê từ Service
        Map<String, Object> thongKeData = thongKeService.getThongKeTongHop();

        // Gửi dữ liệu ra view
        model.addAttribute("thongKe", thongKeData);

        // Trả về file HTML
        return "thongke"; // Trả về templates/thongke.html
    }
}