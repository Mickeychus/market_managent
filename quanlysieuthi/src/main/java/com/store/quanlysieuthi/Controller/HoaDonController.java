package com.store.quanlysieuthi.Controller;

import com.store.quanlysieuthi.Model.HoaDon;
import com.store.quanlysieuthi.Model.HoaDonChiTiet;
import com.store.quanlysieuthi.Model.SanPham;
import com.store.quanlysieuthi.Repository.HoaDonRepository;
import com.store.quanlysieuthi.Repository.SanPhamRepository;
import com.store.quanlysieuthi.Service.AuditLogService;
import jakarta.validation.Valid; // **THÊM IMPORT**
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // **THÊM IMPORT**
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // **THÊM IMPORT**
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // **THÊM IMPORT**
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HoaDonController {

    @Autowired
    private HoaDonRepository hoaDonRepo;
    @Autowired
    private SanPhamRepository sanPhamRepo;
    @Autowired
    private AuditLogService auditLogService;

    // --- Hiển thị danh sách hóa đơn (Giữ nguyên) ---
    @GetMapping("/hoadon")
    public String danhSachHoaDon(Model model) {
        List<HoaDon> danhSach = hoaDonRepo.findAll();
        model.addAttribute("danhSachHD", danhSach);
        return "hoadon-list";
    }

    // --- Hiển thị chi tiết hóa đơn (Giữ nguyên) ---
    @GetMapping("/hoadon/{id}")
    public String xemChiTietHoaDon(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isPresent()) {
            model.addAttribute("hoaDon", optionalHoaDon.get());
            return "hoadon-detail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn với ID: " + id);
            return "redirect:/hoadon";
        }
    }

    // --- **MỚI**: Hiển thị Form Sửa Hóa Đơn ---
    @GetMapping("/hoadon/sua/{id}")
    public String hienThiFormSuaHoaDon(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isPresent()) {
            model.addAttribute("hoaDon", optionalHoaDon.get()); // Gửi hóa đơn ra form
            return "hoadon-form"; // Trả về file templates/hoadon-form.html
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn với ID: " + id);
            return "redirect:/hoadon";
        }
    }

    // --- **MỚI**: Xử lý Lưu Hóa Đơn (Update) ---
    @PostMapping("/hoadon/sua/luu")
    public String capNhatHoaDon(@Valid @ModelAttribute("hoaDon") HoaDon hoaDonForm, // Nhận đối tượng từ form
                                BindingResult bindingResult, // Nhận kết quả validation
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // Chỉ kiểm tra lỗi của tenKhachHang (vì các trường khác readonly)
        if (bindingResult.hasFieldErrors("tenKhachHang")) {
            System.out.println("Lỗi validation tenKhachHang: " + bindingResult.getFieldError("tenKhachHang"));
            // Gửi lại đối tượng (có lỗi) về form
            model.addAttribute("hoaDon", hoaDonForm); 
            // Cần lấy lại đối tượng đầy đủ nếu muốn hiển thị chi tiết (nhưng form này ko cần)
            return "hoadon-form"; // Quay lại form
        }

        try {
            // Lấy hóa đơn gốc từ CSDL
            Optional<HoaDon> optionalHoaDonGoc = hoaDonRepo.findById(hoaDonForm.getId());
            if (optionalHoaDonGoc.isPresent()) {
                HoaDon hoaDonGoc = optionalHoaDonGoc.get();
                String tenCu = hoaDonGoc.getTenKhachHang();
                String tenMoi = hoaDonForm.getTenKhachHang();

                // Chỉ cập nhật trường tenKhachHang
                hoaDonGoc.setTenKhachHang(tenMoi);
                hoaDonRepo.save(hoaDonGoc); // Lưu thay đổi

                // Ghi log
                String details = String.format("ID Hóa đơn: %d, Tên khách hàng cũ: %s, Tên khách hàng mới: %s",
                                                hoaDonGoc.getId(), tenCu, tenMoi);
                auditLogService.logAction("UPDATE_HOADON", details);
                
                redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật tên khách hàng cho hóa đơn #" + hoaDonGoc.getId());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Không tìm thấy hóa đơn để cập nhật.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Xảy ra lỗi khi cập nhật hóa đơn.");
            e.printStackTrace();
            return "redirect:/hoadon/sua/" + hoaDonForm.getId(); // Quay lại form sửa nếu lỗi
        }

        return "redirect:/hoadon"; // Quay về trang danh sách
    }


    // --- Xử lý việc XÓA hóa đơn (Giữ nguyên) ---
    @GetMapping("/hoadon/xoa/{id}")
    @Transactional
    public String xoaHoaDon(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // ... (Mã nguồn xoaHoaDon giữ nguyên như Phần 24) ...
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        String details = "ID Hóa đơn: " + id;

        try {
            if (optionalHoaDon.isPresent()) {
                HoaDon hoaDon = optionalHoaDon.get();
                details = String.format("ID Hóa đơn: %d, Khách hàng: %s, Tổng tiền: %.2f",
                                        hoaDon.getId(), hoaDon.getTenKhachHang(), hoaDon.getTongTien());

                // Trả tồn kho
                for (HoaDonChiTiet chiTiet : hoaDon.getChiTiet()) {
                    String tenSP = chiTiet.getTenSanPham();
                    int soLuongTra = chiTiet.getSoLuong();
                    List<SanPham> sanPhams = sanPhamRepo.findByTenSP(tenSP);
                    if (!sanPhams.isEmpty()) {
                        SanPham spGoc = sanPhams.get(0);
                        spGoc.setSoLuong(spGoc.getSoLuong() + soLuongTra);
                        sanPhamRepo.save(spGoc);
                        System.out.println(">>> Đã trả " + soLuongTra + " tồn kho cho sản phẩm '" + tenSP + "'");
                    } else {
                        System.err.println("!!! Cảnh báo: Không tìm thấy sản phẩm '" + tenSP + "' để trả tồn kho khi xóa hóa đơn #" + id);
                    }
                }

                hoaDonRepo.deleteById(id);
                auditLogService.logAction("DELETE_HOADON", details + " (Đã trả tồn kho)");
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thành công hóa đơn #" + id + " và trả lại tồn kho.");
                System.out.println(">>> Đã xóa hóa đơn #" + id + " và trả tồn kho.");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn #" + id + " để xóa.");
                auditLogService.logAction("DELETE_HOADON_NOT_FOUND", details);
            }
        } catch (Exception e) {
             System.err.println("Lỗi khi xóa hóa đơn #" + id + ": " + e.getMessage());
             redirectAttributes.addFlashAttribute("errorMessage", "Xảy ra lỗi khi xóa hóa đơn #" + id);
             e.printStackTrace();
             auditLogService.logAction("DELETE_HOADON_ERROR", details + " - " + e.getMessage());
        }
        return "redirect:/hoadon";
    }
}