package com.store.quanlysieuthi.Model;

// Lớp này không phải là Entity, chỉ là một POJO (Plain Old Java Object)
// để lưu thông tin tạm thời trong giỏ hàng (Session)
public class CartItem {
    private Long sanPhamId; // Lưu ID của sản phẩm gốc
    private String tenHang;
    private int soLuong;
    private double donGia; // Giá bán tại thời điểm thêm vào giỏ

    // Constructor, Getters, Setters
    public CartItem(Long sanPhamId, String tenHang, int soLuong, double donGia) {
        this.sanPhamId = sanPhamId;
        this.tenHang = tenHang;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public CartItem() {}

    public Long getSanPhamId() { return sanPhamId; }
    public void setSanPhamId(Long sanPhamId) { this.sanPhamId = sanPhamId; }
    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    // Hàm tiện ích tính thành tiền
    public double getThanhTien() {
        return this.soLuong * this.donGia;
    }
}