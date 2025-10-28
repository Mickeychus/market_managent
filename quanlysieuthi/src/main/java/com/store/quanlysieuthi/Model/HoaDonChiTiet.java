package com.store.quanlysieuthi.Model;

import jakarta.persistence.*; // Import tất cả từ jakarta.persistence

@Entity
@Table(name = "hoa_don_chi_tiet") // Đặt tên bảng
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID tự tăng của dòng chi tiết

    // Quan hệ Nhiều-Một: Nhiều Hóa Đơn Chi Tiết thuộc về một Hóa Đơn
    // @JoinColumn(name = "hoa_don_id"): Tạo cột khóa ngoại 'hoa_don_id' trong bảng này để liên kết với bảng 'hoa_don'.
    @ManyToOne(fetch = FetchType.LAZY) // Chỉ tải HoaDon khi cần
    @JoinColumn(name = "hoa_don_id", nullable = false) // Khóa ngoại không được rỗng
    private HoaDon hoaDon;

    // Thông tin sản phẩm tại thời điểm mua hàng
    // Lưu lại tên và giá để hóa đơn không bị ảnh hưởng nếu sản phẩm gốc thay đổi
    @Column(nullable = false)
    private String tenSanPham;

    @Column(nullable = false)
    private int soLuong;

    @Column(nullable = false)
    private double donGia; // Giá bán tại thời điểm mua

    // Có thể thêm ID sản phẩm gốc nếu muốn dễ dàng truy vết
    // private Long sanPhamGocId;

    // Constructor rỗng
    public HoaDonChiTiet() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    // public Long getSanPhamGocId() { return sanPhamGocId;}
    // public void setSanPhamGocId(Long sanPhamGocId) { this.sanPhamGocId = sanPhamGocId; }
}