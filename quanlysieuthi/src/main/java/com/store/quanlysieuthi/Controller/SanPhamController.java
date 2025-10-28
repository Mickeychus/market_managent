package com.store.quanlysieuthi.Controller;

import com.store.quanlysieuthi.Model.SanPham;
import com.store.quanlysieuthi.Repository.SanPhamRepository;
import com.store.quanlysieuthi.Service.AuditLogService; // **THÊM IMPORT**
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; // **ĐẢM BẢO CÓ IMPORT NÀY**
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class SanPhamController {

    @Autowired
    private SanPhamRepository sanPhamRepo;

    @Autowired // **THÊM**: Tiêm AuditLogService
    private AuditLogService auditLogService;

    // --- Hiển thị danh sách sản phẩm ---
    @GetMapping("/sanpham")
    public String hienThiDanhSach(Model model) {
        List<SanPham> danhSach = sanPhamRepo.findAll();
        model.addAttribute("danhSachSP", danhSach);
        return "sanpham-list";
    }

    // --- Hiển thị form để thêm sản phẩm mới ---
    @GetMapping("/sanpham/them")
    public String hienThiFormThem(Model model) {
        model.addAttribute("sanPhamMoi", new SanPham());
        model.addAttribute("pageTitle", "Thêm Sản phẩm Mới");
        return "sanpham-form";
    }

    // --- Hiển thị form để SỬA sản phẩm ---
    @GetMapping("/sanpham/sua/{id}")
    public String hienThiFormSua(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<SanPham> optionalSanPham = sanPhamRepo.findById(id);
        if (optionalSanPham.isPresent()) {
            model.addAttribute("sanPhamMoi", optionalSanPham.get());
            model.addAttribute("pageTitle", "Sửa Sản phẩm (ID: " + id + ")");
            return "sanpham-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm với ID: " + id);
            return "redirect:/sanpham";
        }
    }

    // --- Xử lý việc LƯU sản phẩm (Cả Thêm và Sửa) với Validation và Audit Log ---
    @PostMapping("/sanpham/luu")
    public String luuSanPham(@Valid @ModelAttribute("sanPhamMoi") SanPham sanPham,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("Lỗi validation: " + bindingResult.getAllErrors());
            model.addAttribute("pageTitle", (sanPham.getId() == null) ? "Thêm Sản phẩm Mới (Lỗi)" : "Sửa Sản phẩm (Lỗi)");
            return "sanpham-form";
        }

        try {
            boolean isNew = (sanPham.getId() == null || sanPham.getId() == 0);
            SanPham savedSanPham = sanPhamRepo.save(sanPham); // Lưu và lấy lại đối tượng

            // **GHI LOG**: Ghi lại hành động lưu
            String action = isNew ? "CREATE_PRODUCT" : "UPDATE_PRODUCT";
            String details = String.format("ID: %d, Tên: %s, SL: %d, Giá nhập: %.2f, Giá bán: %.2f",
                                           savedSanPham.getId(), savedSanPham.getTenSP(), savedSanPham.getSoLuong() != null ? savedSanPham.getSoLuong() : 0, // Kiểm tra null cho Integer
                                           savedSanPham.getGiaNhap() != null ? savedSanPham.getGiaNhap() : 0.0, // Kiểm tra null cho Double
                                           savedSanPham.getGiaDeXuat() != null ? savedSanPham.getGiaDeXuat() : 0.0); // Kiểm tra null cho Double
            auditLogService.logAction(action, details);

            redirectAttributes.addFlashAttribute("successMessage",
                    isNew ? "Đã thêm sản phẩm thành công!" : "Đã cập nhật sản phẩm thành công!");
            return "redirect:/sanpham";

        } catch (Exception e) {
             System.err.println("Lỗi khi lưu sản phẩm: " + e.getMessage());
             redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi lưu sản phẩm!");
             e.printStackTrace();
             // **GHI LOG**: Ghi log lỗi (tùy chọn)
             auditLogService.logAction("SAVE_PRODUCT_ERROR", "Lỗi khi lưu sản phẩm: " + (sanPham.getTenSP() != null ? sanPham.getTenSP() : "[Chưa có tên]") + " - " + e.getMessage());

             model.addAttribute("pageTitle", (sanPham.getId() == null) ? "Thêm Sản phẩm Mới (Lỗi Lưu)" : "Sửa Sản phẩm (Lỗi Lưu)");
             model.addAttribute("sanPhamMoi", sanPham); // Gửi lại đối tượng để giữ dữ liệu
             return "sanpham-form";
        }
    }

    // --- Xử lý việc XOÁ sản phẩm với Audit Log ---
    @GetMapping("/sanpham/xoa/{id}")
    public String xoaSanPham(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<SanPham> optSanPham = sanPhamRepo.findById(id); // Lấy thông tin trước khi xóa
        String details = "ID: " + id; // Mặc định chỉ log ID

        try {
            if (optSanPham.isPresent()) {
                SanPham sanPhamToDelete = optSanPham.get();
                // Cập nhật details với thông tin đầy đủ hơn
                details = String.format("ID: %d, Tên: %s", sanPhamToDelete.getId(), sanPhamToDelete.getTenSP());

                sanPhamRepo.deleteById(id);

                // **GHI LOG**: Ghi log sau khi xóa thành công
                auditLogService.logAction("DELETE_PRODUCT", details);

                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thành công sản phẩm #" + id);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm #" + id + " để xóa.");
                // **GHI LOG**: Ghi log không tìm thấy (tùy chọn)
                auditLogService.logAction("DELETE_PRODUCT_NOT_FOUND", details);
            }
        } catch (Exception e) {
             System.err.println("Lỗi khi xóa sản phẩm #" + id + ": " + e.getMessage());
             redirectAttributes.addFlashAttribute("errorMessage", "Xảy ra lỗi khi xóa sản phẩm #" + id);
             e.printStackTrace();
             // **GHI LOG**: Ghi log lỗi xóa
             auditLogService.logAction("DELETE_PRODUCT_ERROR", details + " - " + e.getMessage());
        }
        return "redirect:/sanpham";
    }
}