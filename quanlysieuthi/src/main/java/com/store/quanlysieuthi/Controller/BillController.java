package com.store.quanlysieuthi.Controller;

// Import cần thiết
import com.store.quanlysieuthi.Model.CartItem;
import com.store.quanlysieuthi.Model.HoaDon;
import com.store.quanlysieuthi.Model.HoaDonChiTiet;
import com.store.quanlysieuthi.Model.SanPham;
import com.store.quanlysieuthi.Repository.HoaDonRepository;
import com.store.quanlysieuthi.Repository.SanPhamRepository;
import com.store.quanlysieuthi.Service.AuditLogService; // **THÊM IMPORT**
import com.store.quanlysieuthi.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired; // **ĐẢM BẢO CÓ IMPORT NÀY**
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional; // Đảm bảo import này tồn tại

@Controller
public class BillController {

    @Autowired
    private SanPhamRepository sanPhamRepo;

    @Autowired
    private HoaDonRepository hoaDonRepo;

    @Autowired
    private CartService cartService;

    @Autowired // **THÊM**: Tiêm AuditLogService
    private AuditLogService auditLogService;

    // --- Hiển thị trang bán hàng chính ---
    @GetMapping("/banhang")
    public String trangBanHang(Model model) {
        List<SanPham> danhSachSP = sanPhamRepo.findAll();
        model.addAttribute("danhSachSP", danhSachSP);

        List<CartItem> gioHang = cartService.getItems();
        model.addAttribute("gioHang", gioHang);

        double tongTien = cartService.getTotal();
        model.addAttribute("tongTien", tongTien);

        return "banhang";
    }

    // --- Xử lý việc thêm sản phẩm vào giỏ hàng ---
    @PostMapping("/banhang/them")
    public String themVaoGio(
            @RequestParam("sanPhamId") Long sanPhamId,
            @RequestParam("soLuongMua") int soLuongMua,
            RedirectAttributes redirectAttributes)
    {
        boolean success = cartService.addItem(sanPhamId, soLuongMua);
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thêm vào giỏ thất bại! Không đủ số lượng tồn kho.");
            System.err.println("Thêm vào giỏ thất bại (có thể do hết hàng)");
        }
        return "redirect:/banhang";
    }

    // --- Xử lý việc XOÁ mặt hàng khỏi giỏ hàng ---
    @GetMapping("/banhang/xoa/{sanPhamId}") // Thay billId thành sanPhamId
    public String xoaKhoiGio(@PathVariable("sanPhamId") Long sanPhamId) {
        // **THAY ĐỔI**: Gọi CartService để xóa item (dựa vào sanPhamId)
        cartService.removeItem(sanPhamId);
        return "redirect:/banhang";
    }

    // --- Xử lý việc CẬP NHẬT số lượng mặt hàng trong giỏ hàng ---
    @PostMapping("/banhang/capnhat")
    public String capNhatSoLuong(
            @RequestParam("sanPhamId") Long sanPhamId, // Thay billId thành sanPhamId
            @RequestParam("soLuongMoi") int soLuongMoi,
            RedirectAttributes redirectAttributes)
    {
         // **THAY ĐỔI**: Gọi CartService để cập nhật
         boolean success = cartService.updateItem(sanPhamId, soLuongMoi);
         if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật giỏ thất bại! Không đủ số lượng tồn kho.");
            System.err.println("Cập nhật giỏ thất bại (có thể do hết hàng)");
         }
        return "redirect:/banhang";
    }

     // --- **MỚI**: Xử lý việc LƯU HÓA ĐƠN với Audit Log ---
    @PostMapping("/banhang/luuhoadon")
    public String luuHoaDon(@RequestParam("tenKhachHang") String tenKhachHang, RedirectAttributes redirectAttributes) {
        List<CartItem> gioHang = cartService.getItems();

        // --- Kiểm tra giỏ hàng và tên khách ---
        if (gioHang.isEmpty()) {
             redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Giỏ hàng đang trống!");
            System.err.println("Lỗi: Giỏ hàng trống.");
             // Ghi log lỗi (tùy chọn)
             auditLogService.logAction("CREATE_HOADON_ERROR", "Thất bại: Giỏ hàng trống.");
            return "redirect:/banhang";
        }
        if (tenKhachHang == null || tenKhachHang.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Vui lòng nhập tên khách hàng!");
            System.err.println("Lỗi: Chưa nhập tên khách hàng.");
             // Ghi log lỗi (tùy chọn)
             auditLogService.logAction("CREATE_HOADON_ERROR", "Thất bại: Chưa nhập tên khách hàng.");
            return "redirect:/banhang";
        }
        // --- Kết thúc kiểm tra ---

        String detailsForLog = ""; // Chuẩn bị chuỗi log

        try {
            HoaDon hoaDon = new HoaDon();
            hoaDon.setTenKhachHang(tenKhachHang.trim());

            StringBuilder detailsBuilder = new StringBuilder();
            detailsBuilder.append(String.format("Khách hàng: %s, Tổng tiền: %.2f, Chi tiết: [",
                                                  hoaDon.getTenKhachHang(), cartService.getTotal())); // Lấy tổng tiền từ cartService

            for (CartItem item : gioHang) {
                HoaDonChiTiet chiTiet = new HoaDonChiTiet();
                chiTiet.setTenSanPham(item.getTenHang());
                chiTiet.setSoLuong(item.getSoLuong());
                chiTiet.setDonGia(item.getDonGia());
                // chiTiet.setSanPhamGocId(item.getSanPhamId()); // Có thể lưu ID SP gốc
                hoaDon.addChiTiet(chiTiet); // Tự động set quan hệ và tính tổng tiền

                // Thêm vào chuỗi details log
                detailsBuilder.append(String.format("{SP: %s, SL: %d, ĐG: %.2f}, ",
                                        item.getTenHang(), item.getSoLuong(), item.getDonGia()));
            }
             // Xóa dấu phẩy cuối và đóng ngoặc vuông cho log
             if (!gioHang.isEmpty()) {
                 detailsBuilder.setLength(detailsBuilder.length() - 2); // Xóa ", "
             }
             detailsBuilder.append("]");
             detailsForLog = detailsBuilder.toString(); // Gán chuỗi log hoàn chỉnh


            // Lưu Hóa đơn (Cascade tự lưu chi tiết)
            HoaDon savedHoaDon = hoaDonRepo.save(hoaDon);

            // Xóa giỏ hàng Session mà không trả tồn kho
            cartService.clearCartWithoutRestock();

             // **GHI LOG**: Ghi log sau khi lưu thành công
             auditLogService.logAction("CREATE_HOADON", "ID Hóa đơn: " + savedHoaDon.getId() + ", " + detailsForLog);


            System.out.println(">>> Đã lưu hóa đơn thành công! ID: " + savedHoaDon.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu hóa đơn thành công! ID: " + savedHoaDon.getId());

        } catch (Exception e) {
             System.err.println("Lỗi khi lưu hóa đơn: " + e.getMessage());
             redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi lưu hóa đơn!");
             e.printStackTrace();
              // **GHI LOG**: Ghi log lỗi lưu hóa đơn
             auditLogService.logAction("CREATE_HOADON_ERROR", "Khách hàng: " + tenKhachHang + " - " + e.getMessage());
        }

        return "redirect:/banhang"; // Luôn quay về trang bán hàng sau khi xử lý
    }
}