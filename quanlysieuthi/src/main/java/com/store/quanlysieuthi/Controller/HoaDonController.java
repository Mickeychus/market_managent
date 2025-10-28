package com.store.quanlysieuthi.Controller;

import com.store.quanlysieuthi.Model.HoaDon;
import com.store.quanlysieuthi.Model.HoaDonChiTiet;
import com.store.quanlysieuthi.Model.SanPham;
import com.store.quanlysieuthi.Repository.HoaDonRepository;
import com.store.quanlysieuthi.Repository.SanPhamRepository;
import com.store.quanlysieuthi.Service.AuditLogService; // **THÊM IMPORT**
import org.springframework.beans.factory.annotation.Autowired; // **ĐẢM BẢO CÓ IMPORT NÀY**
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HoaDonController {

    @Autowired
    private HoaDonRepository hoaDonRepo;

    @Autowired
    private SanPhamRepository sanPhamRepo;

    @Autowired // **THÊM**: Tiêm AuditLogService
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

    // --- Xử lý việc XÓA hóa đơn (Chỉ Admin) VÀ TRẢ TỒN KHO với Audit Log ---
    @GetMapping("/hoadon/xoa/{id}")
    @Transactional
    public String xoaHoaDon(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id); // Lấy thông tin trước khi xóa
        String details = "ID Hóa đơn: " + id; // Mặc định log ID

        try {
            if (optionalHoaDon.isPresent()) {
                HoaDon hoaDon = optionalHoaDon.get();
                // Cập nhật details đầy đủ hơn
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

                // Xóa hóa đơn
                hoaDonRepo.deleteById(id);

                // **GHI LOG**: Ghi log sau khi xóa thành công
                auditLogService.logAction("DELETE_HOADON", details + " (Đã trả tồn kho)");

                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thành công hóa đơn #" + id + " và trả lại tồn kho.");
                System.out.println(">>> Đã xóa hóa đơn #" + id + " và trả tồn kho.");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn #" + id + " để xóa.");
                 // **GHI LOG**: Ghi log không tìm thấy
                auditLogService.logAction("DELETE_HOADON_NOT_FOUND", details);
            }
        } catch (Exception e) {
             System.err.println("Lỗi khi xóa hóa đơn #" + id + ": " + e.getMessage());
             redirectAttributes.addFlashAttribute("errorMessage", "Xảy ra lỗi khi xóa hóa đơn #" + id);
             e.printStackTrace();
             // **GHI LOG**: Ghi log lỗi xóa
             auditLogService.logAction("DELETE_HOADON_ERROR", details + " - " + e.getMessage());
             // Transactional sẽ rollback tồn kho
        }
        return "redirect:/hoadon";
    }
}