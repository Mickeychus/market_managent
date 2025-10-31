package com.store.quanlysieuthi.Service;

import com.store.quanlysieuthi.Model.CartItem;
import com.store.quanlysieuthi.Model.SanPham;
import com.store.quanlysieuthi.Repository.SanPhamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Kích hoạt Mockito
class CartServiceTest {

    @Mock
    private SanPhamRepository sanPhamRepo; // Giả lập Repository

    @InjectMocks
    private CartService cartService; // Đối tượng thật cần test

    private SanPham sanPham1;

    @BeforeEach
    void setUp() {
        // Chuẩn bị một sản phẩm mẫu
        sanPham1 = new SanPham();
        sanPham1.setId(1L);
        sanPham1.setTenSP("Táo");
        sanPham1.setSoLuong(10); // Tồn kho 10
        sanPham1.setGiaDeXuat(50.0);
        sanPham1.setGiaNhap(30.0);

        // Reset giỏ hàng (CartService) trước mỗi test
        // Bằng cách tạo một instance mới (Mockito làm điều này)
        // hoặc gọi cartService.clearCartWithoutRestock(); (nếu cần)
        cartService.clearCartWithoutRestock(); // Đảm bảo giỏ hàng trống
    }

    // --- Test Thêm vào giỏ (Thành công) ---
    @Test
    void testAddItem_Success() {
        // 1. Chuẩn bị
        // Khi tìm SP 1 -> trả về sanPham1
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));
        // Khi lưu SP (sau khi trừ tồn kho) -> trả về SP đã lưu
        when(sanPhamRepo.save(any(SanPham.class))).thenReturn(sanPham1);

        // 2. Thực thi
        boolean result = cartService.addItem(1L, 3); // Mua 3

        // 3. Kiểm chứng
        assertTrue(result); // Phải thành công
        assertEquals(1, cartService.getItems().size()); // Giỏ hàng có 1 món
        assertEquals(3, cartService.getItems().get(0).getSoLuong()); // Số lượng là 3
        assertEquals(7, sanPham1.getSoLuong()); // Tồn kho còn 7
        // Kiểm tra hàm save (để cập nhật tồn kho) được gọi 1 lần
        verify(sanPhamRepo, times(1)).save(sanPham1);
    }

    // --- Test Thêm vào giỏ (Hết hàng) ---
    @Test
    void testAddItem_OutOfStock() {
        // 1. Chuẩn bị
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));

        // 2. Thực thi
        boolean result = cartService.addItem(1L, 15); // Mua 15 (chỉ có 10)

        // 3. Kiểm chứng
        assertFalse(result); // Phải thất bại
        assertEquals(0, cartService.getItems().size()); // Giỏ hàng rỗng
        assertEquals(10, sanPham1.getSoLuong()); // Tồn kho giữ nguyên 10
        verify(sanPhamRepo, times(0)).save(any(SanPham.class)); // Không lưu
    }
    
    // --- Test Thêm dồn (cộng dồn số lượng) ---
    @Test
    void testAddItem_AddMore_Success() {
        // 1. Chuẩn bị
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));
        when(sanPhamRepo.save(any(SanPham.class))).thenReturn(sanPham1);

        // 2. Thực thi
        cartService.addItem(1L, 3); // Thêm 3 (Tồn kho còn 7)
        boolean result2 = cartService.addItem(1L, 5); // Thêm 5 (Tồn kho còn 2)

        // 3. Kiểm chứng
        assertTrue(result2); // Lần thêm 2 thành công
        assertEquals(1, cartService.getItems().size()); // Giỏ hàng vẫn chỉ có 1 món
        assertEquals(8, cartService.getItems().get(0).getSoLuong()); // Số lượng tổng là 8
        assertEquals(2, sanPham1.getSoLuong()); // Tồn kho còn 2
        verify(sanPhamRepo, times(2)).save(sanPham1); // Gọi save 2 lần
    }

    // --- Test Xóa khỏi giỏ ---
    @Test
    void testRemoveItem() {
        // 1. Chuẩn bị
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));
        when(sanPhamRepo.save(any(SanPham.class))).thenReturn(sanPham1);
        cartService.addItem(1L, 4); // Thêm 4 vào giỏ (Tồn kho còn 6)
        
        // 2. Thực thi
        cartService.removeItem(1L); // Xóa khỏi giỏ

        // 3. Kiểm chứng
        assertEquals(0, cartService.getItems().size()); // Giỏ hàng rỗng
        assertEquals(10, sanPham1.getSoLuong()); // Tồn kho trả về 10
        verify(sanPhamRepo, times(2)).save(sanPham1); // Gọi save 1 lần khi thêm, 1 lần khi xóa
    }

    // --- Test Cập nhật số lượng (Tăng) ---
    @Test
    void testUpdateItem_Increase_Success() {
        // 1. Chuẩn bị
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));
        when(sanPhamRepo.save(any(SanPham.class))).thenReturn(sanPham1);
        cartService.addItem(1L, 2); // Thêm 2 (Tồn kho 8)
        
        // 2. Thực thi
        boolean result = cartService.updateItem(1L, 5); // Cập nhật lên 5 (chênh lệch -3)

        // 3. Kiểm chứng
        assertTrue(result);
        assertEquals(5, cartService.getItems().get(0).getSoLuong()); // Giỏ hàng là 5
        assertEquals(5, sanPham1.getSoLuong()); // Tồn kho 10 - 2 - 3 = 5
        verify(sanPhamRepo, times(2)).save(sanPham1);
    }
    
    // --- Test Cập nhật số lượng (Giảm) ---
    @Test
    void testUpdateItem_Decrease_Success() {
        // 1. Chuẩn bị
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));
        when(sanPhamRepo.save(any(SanPham.class))).thenReturn(sanPham1);
        cartService.addItem(1L, 8); // Thêm 8 (Tồn kho 2)
        
        // 2. Thực thi
        boolean result = cartService.updateItem(1L, 3); // Cập nhật xuống 3 (chênh lệch +5)

        // 3. Kiểm chứng
        assertTrue(result);
        assertEquals(3, cartService.getItems().get(0).getSoLuong()); // Giỏ hàng là 3
        assertEquals(7, sanPham1.getSoLuong()); // Tồn kho 10 - 8 + 5 = 7
        verify(sanPhamRepo, times(2)).save(sanPham1);
    }
    
    // --- Test Cập nhật số lượng (Vượt tồn kho) ---
    @Test
    void testUpdateItem_Increase_OutOfStock() {
        // 1. Chuẩn bị
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));
        when(sanPhamRepo.save(any(SanPham.class))).thenReturn(sanPham1);
        cartService.addItem(1L, 5); // Thêm 5 (Tồn kho 5)
        
        // 2. Thực thi
        // Cập nhật lên 11 (chênh lệch -6, tồn kho chỉ còn 5, không đủ)
        boolean result = cartService.updateItem(1L, 11); 

        // 3. Kiểm chứng
        assertFalse(result); // Thất bại
        assertEquals(5, cartService.getItems().get(0).getSoLuong()); // Giỏ hàng vẫn là 5
        assertEquals(5, sanPham1.getSoLuong()); // Tồn kho vẫn là 5
        verify(sanPhamRepo, times(1)).save(sanPham1); // Chỉ gọi save 1 lần lúc thêm
    }

    // --- Test Xóa giỏ hàng (Sau khi thanh toán) ---
    @Test
    void testClearCartWithoutRestock() {
        // 1. Chuẩn bị
        when(sanPhamRepo.findById(1L)).thenReturn(Optional.of(sanPham1));
        when(sanPhamRepo.save(any(SanPham.class))).thenReturn(sanPham1);
        cartService.addItem(1L, 6); // Thêm 6 (Tồn kho 4)
        
        // 2. Thực thi
        cartService.clearCartWithoutRestock(); // Xóa không trả tồn kho

        // 3. Kiểm chứng
        assertEquals(0, cartService.getItems().size()); // Giỏ hàng rỗng
        assertEquals(4, sanPham1.getSoLuong()); // Tồn kho vẫn là 4
        verify(sanPhamRepo, times(1)).save(sanPham1); // Chỉ gọi save 1 lần lúc thêm
    }
}