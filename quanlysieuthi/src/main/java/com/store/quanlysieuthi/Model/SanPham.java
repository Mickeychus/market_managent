package com.store.quanlysieuthi.Model; // Gói Model viết hoa

// Import các annotation validation
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 100, message = "Tên sản phẩm phải từ 2 đến 100 ký tự")
    private String tenSP;

    @NotBlank(message = "Ngày nhập không được để trống")
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$", message = "Định dạng ngày phải là dd/MM/yyyy")
    private String ngayNhap;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer soLuong; // Đổi sang Integer

    @NotBlank(message = "Phân loại không được để trống")
    private String phanLoai;

    @NotNull(message = "Giá nhập không được để trống")
    @Min(value = 0, message = "Giá nhập không được âm")
    private Double giaNhap; // Đổi sang Double

    @NotNull(message = "Giá đề xuất không được để trống")
    @Min(value = 0, message = "Giá đề xuất không được âm")
    private Double giaDeXuat; // Đổi sang Double

    // Constructor rỗng (Bắt buộc cho JPA)
    public SanPham() {}

    // Constructor (Tùy chọn, để dễ sử dụng)
    public SanPham(String tenSP, String ngayNhap, Integer soLuong, String phanLoai, Double giaNhap, Double giaDeXuat) {
        this.tenSP = tenSP;
        this.ngayNhap = ngayNhap;
        this.soLuong = soLuong;
        this.phanLoai = phanLoai;
        this.giaNhap = giaNhap;
        this.giaDeXuat = giaDeXuat;
    }

    // --- Getters và Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public String getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(String ngayNhap) { this.ngayNhap = ngayNhap; }
    public Integer getSoLuong() { return soLuong; } // Kiểu Integer
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; } // Kiểu Integer
    public String getPhanLoai() { return phanLoai; }
    public void setPhanLoai(String phanLoai) { this.phanLoai = phanLoai; }
    public Double getGiaNhap() { return giaNhap; } // Kiểu Double
    public void setGiaNhap(Double giaNhap) { this.giaNhap = giaNhap; } // Kiểu Double
    public Double getGiaDeXuat() { return giaDeXuat; } // Kiểu Double
    public void setGiaDeXuat(Double giaDeXuat) { this.giaDeXuat = giaDeXuat; } // Kiểu Double
    // --- Kết thúc Getters và Setters ---
}