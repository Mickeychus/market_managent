package com.store.quanlysieuthi.Model; // Gói Model viết hoa

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// import jakarta.persistence.Table; // Có thể dùng nếu muốn tên bảng khác tên lớp

@Entity
// @Table(name = "hoa_don") // Bỏ comment dòng này nếu muốn tên bảng là "hoa_don" thay vì "bill"
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID tự tăng cho mỗi dòng bill (mỗi mặt hàng)

    // Chúng ta sẽ cần thêm Mã hóa đơn chung và Tên khách hàng sau
    // private String maHoaDon;
    // private String tenKhachHang;

    private String tenHang;
    private int soLuong;
    private double giaBan;

    // JPA cần constructor rỗng
    public Bill() {
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }
}