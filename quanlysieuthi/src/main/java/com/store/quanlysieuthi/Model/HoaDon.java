package com.store.quanlysieuthi.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // **THÊM IMPORT**
import jakarta.validation.constraints.Size;   // **THÊM IMPORT**
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khách hàng không được để trống") // **THÊM**
    @Size(min = 2, max = 100, message = "Tên khách hàng phải từ 2 đến 100 ký tự") // **THÊM**
    @Column(nullable = false)
    private String tenKhachHang;

    @Column(nullable = false)
    private LocalDateTime ngayTao;

    private double tongTien;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> chiTiet = new ArrayList<>();

    // Constructor rỗng
    public HoaDon() {
        this.ngayTao = LocalDateTime.now();
    }

    // Getters and Setters (Giữ nguyên)
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

    // Hàm tiện ích (Giữ nguyên)
    public void addChiTiet(HoaDonChiTiet item) {
        chiTiet.add(item);
        item.setHoaDon(this);
        recalculateTongTien();
    }
    public void recalculateTongTien() {
        this.tongTien = chiTiet.stream()
                               .mapToDouble(item -> item.getSoLuong() * item.getDonGia())
                               .sum();
    }
}