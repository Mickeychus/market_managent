package com.store.quanlysieuthi.Service;

import com.store.quanlysieuthi.Model.HoaDon;
import com.store.quanlysieuthi.Model.HoaDonChiTiet;
import com.store.quanlysieuthi.Model.SanPham;
import com.store.quanlysieuthi.Repository.HoaDonRepository;
import com.store.quanlysieuthi.Repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThongKeService {

    @Autowired
    private SanPhamRepository sanPhamRepo;

    @Autowired
    private HoaDonRepository hoaDonRepo;

    // Lấy dữ liệu thống kê tổng hợp
    public Map<String, Object> getThongKeTongHop() {
        Map<String, Object> thongKe = new HashMap<>();

        // 1. Thông tin Nhập hàng (từ SanPham)
        List<SanPham> allSanPham = sanPhamRepo.findAll();
        long tongSoLuongNhap = allSanPham.stream()
                                        .mapToLong(SanPham::getSoLuong) // Giả định soLuong hiện tại là tồn kho ban đầu
                                        .sum();
        double tongTienNhap = allSanPham.stream()
                                       .mapToDouble(sp -> sp.getGiaNhap() * sp.getSoLuong()) // Cần số lượng nhập ban đầu, tạm dùng tồn kho
                                       .sum(); // Logic này cần xem lại nếu có nhập hàng sau này

        thongKe.put("tongSoLuongNhap", tongSoLuongNhap); // Số lượng tồn kho hiện tại
        thongKe.put("tongTienNhap", tongTienNhap); // Giá trị tồn kho theo giá nhập

        // 2. Thông tin Bán hàng (từ HoaDon và HoaDonChiTiet)
        List<HoaDon> allHoaDon = hoaDonRepo.findAll();
        long tongSoLuongBan = allHoaDon.stream()
                                       .flatMap(hd -> hd.getChiTiet().stream()) // Lấy tất cả chi tiết từ tất cả hóa đơn
                                       .mapToLong(HoaDonChiTiet::getSoLuong)
                                       .sum();
        double tongTienBan = allHoaDon.stream()
                                      .mapToDouble(HoaDon::getTongTien) // Tổng doanh thu
                                      .sum();

        thongKe.put("tongSoLuongBan", tongSoLuongBan); // Tổng số sản phẩm đã bán
        thongKe.put("tongTienBan", tongTienBan); // Tổng doanh thu

        // 3. Tính Lãi (ước tính)
        // Cách tính lãi chính xác cần biết giá nhập của từng sản phẩm đã bán.
        // Cách đơn giản (ước tính): Tổng Tiền Bán - (Tổng số lượng bán * Giá nhập trung bình) -> Rất không chính xác
        // Cách khác (ước tính): Tổng Tiền Bán - Tổng Tiền Nhập (của tồn kho hiện tại) -> Cũng không chính xác
        // Tạm thời tính lãi dựa trên giá bán và giá nhập của *tồn kho hiện tại* (logic giống SanPhamDAO cũ)
        // Lưu ý: Logic lãi này cần được cải thiện đáng kể cho thực tế.
         double tongLaiUocTinh = allSanPham.stream()
               .mapToDouble(sp -> (sp.getGiaDeXuat() - sp.getGiaNhap()) * sp.getSoLuong()) // Lãi trên *tồn kho* hiện tại
               .sum();
         // Hoặc tính lãi dựa trên hóa đơn (cần thêm giá nhập vào HoaDonChiTiet)

        thongKe.put("tongLaiUocTinh", tongLaiUocTinh); // Tạm tính lãi trên tồn kho

        thongKe.put("soLuongSanPhamTrongKho", allSanPham.size()); // Số loại sản phẩm
        thongKe.put("soLuongHoaDon", allHoaDon.size()); // Số hóa đơn đã tạo


        return thongKe;
    }
}