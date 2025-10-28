package com.store.quanlysieuthi.Model;

import jakarta.persistence.*; // Import tất cả từ jakarta.persistence
import java.time.LocalDateTime; // Dùng kiểu dữ liệu ngày giờ chuẩn
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hoa_don") // Đặt tên bảng là hoa_don
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID tự tăng của hóa đơn

    @Column(nullable = false) // Tên khách hàng không được rỗng
    private String tenKhachHang;

    @Column(nullable = false)
    private LocalDateTime ngayTao; // Thời gian tạo hóa đơn

    private double tongTien;

    // Quan hệ Một-Nhiều: Một Hóa Đơn có nhiều Hóa Đơn Chi Tiết
    // mappedBy = "hoaDon": chỉ ra rằng thuộc tính 'hoaDon' trong lớp HoaDonChiTiet quản lý mối quan hệ này.
    // cascade = CascadeType.ALL: Khi lưu/xóa HoaDon, các HoaDonChiTiet liên quan cũng được lưu/xóa theo.
    // orphanRemoval = true: Khi xóa một HoaDonChiTiet khỏi danh sách 'chiTiet', nó cũng sẽ bị xóa khỏi CSDL.
    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> chiTiet = new ArrayList<>(); // Danh sách các mặt hàng trong hóa đơn

    // Constructor rỗng
    public HoaDon() {
        this.ngayTao = LocalDateTime.now(); // Tự động đặt ngày giờ hiện tại khi tạo
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public List<HoaDonChiTiet> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<HoaDonChiTiet> chiTiet) { this.chiTiet = chiTiet; }

    // Hàm tiện ích để thêm chi tiết và tự động tính tổng tiền
    public void addChiTiet(HoaDonChiTiet item) {
        chiTiet.add(item);
        item.setHoaDon(this); // Thiết lập quan hệ ngược lại
        recalculateTongTien(); // Tính lại tổng tiền
    }

    // Hàm tiện ích để tính lại tổng tiền
    public void recalculateTongTien() {
        this.tongTien = chiTiet.stream()
                               .mapToDouble(item -> item.getSoLuong() * item.getDonGia())
                               .sum();
    }
}