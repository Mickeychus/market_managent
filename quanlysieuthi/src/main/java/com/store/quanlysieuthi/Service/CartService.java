package com.store.quanlysieuthi.Service; // Gói Service viết hoa

import com.store.quanlysieuthi.Model.CartItem;
import com.store.quanlysieuthi.Model.SanPham;
import com.store.quanlysieuthi.Repository.SanPhamRepository;
import jakarta.servlet.http.HttpSession; // Không cần thiết khi dùng @SessionScope
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope; // Quan trọng

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope // Đảm bảo mỗi người dùng có giỏ hàng riêng
public class CartService {

    private List<CartItem> items = new ArrayList<>(); // Giỏ hàng trong session

    @Autowired
    private SanPhamRepository sanPhamRepo;

    // Lấy tất cả items
    public List<CartItem> getItems() {
        return items;
    }

    // Thêm item vào giỏ
    public boolean addItem(Long sanPhamId, int quantity) {
        Optional<SanPham> optSp = sanPhamRepo.findById(sanPhamId);
        if (optSp.isPresent() && quantity > 0) {
            SanPham sp = optSp.get();
            // Kiểm tra tồn kho
            if (sp.getSoLuong() >= quantity) {
                // Kiểm tra xem đã có trong giỏ chưa
                Optional<CartItem> existingItem = findItemById(sanPhamId);
                if (existingItem.isPresent()) {
                    // Nếu có, cộng dồn (kiểm tra lại tồn kho)
                    CartItem cartItem = existingItem.get();
                    if (sp.getSoLuong() >= cartItem.getSoLuong() + quantity) {
                        cartItem.setSoLuong(cartItem.getSoLuong() + quantity);
                        updateSanPhamStock(sp, -quantity); // Trừ tồn kho
                        return true;
                    } else {
                        System.err.println("Không đủ tồn kho để thêm số lượng này.");
                        return false;
                    }
                } else {
                    // Nếu chưa có, tạo mới
                    CartItem newItem = new CartItem(sp.getId(), sp.getTenSP(), quantity, sp.getGiaDeXuat());
                    items.add(newItem);
                    updateSanPhamStock(sp, -quantity); // Trừ tồn kho
                    return true;
                }
            } else {
                 System.err.println("Không đủ tồn kho ban đầu.");
                 return false;
            }
        }
         System.err.println("Sản phẩm không tồn tại hoặc số lượng không hợp lệ.");
         return false;
    }

    // Xóa item khỏi giỏ
    public void removeItem(Long sanPhamId) {
        Optional<CartItem> optItem = findItemById(sanPhamId);
        if(optItem.isPresent()){
            CartItem itemToRemove = optItem.get();
            Optional<SanPham> optSp = sanPhamRepo.findById(sanPhamId);
            if(optSp.isPresent()){
                updateSanPhamStock(optSp.get(), itemToRemove.getSoLuong()); // Cộng trả tồn kho
            }
            items.remove(itemToRemove);
        }
    }

    // Cập nhật số lượng item
    public boolean updateItem(Long sanPhamId, int newQuantity) {
        Optional<CartItem> optItem = findItemById(sanPhamId);
        Optional<SanPham> optSp = sanPhamRepo.findById(sanPhamId);

        if (optItem.isPresent() && optSp.isPresent() && newQuantity > 0) {
            CartItem cartItem = optItem.get();
            SanPham sp = optSp.get();
            int oldQuantity = cartItem.getSoLuong();
            int diff = newQuantity - oldQuantity; // Chênh lệch

            // Kiểm tra tồn kho: Tồn kho hiện tại >= Chênh lệch (nếu là thêm)
            if (sp.getSoLuong() >= diff) {
                cartItem.setSoLuong(newQuantity);
                updateSanPhamStock(sp, -diff); // Cập nhật tồn kho theo chênh lệch
                return true;
            } else {
                System.err.println("Không đủ tồn kho để cập nhật.");
                return false;
            }
        } else if (newQuantity <= 0) {
            // Nếu số lượng mới <= 0 thì coi như xoá
            removeItem(sanPhamId);
            return true;
        }
        System.err.println("Sản phẩm không có trong giỏ hoặc không tồn tại.");
        return false;
    }

    // Hàm cũ, trả lại tồn kho (có thể dùng khi hủy đơn hàng)
    public void clearCart() {
        for(CartItem item : new ArrayList<>(items)) { // Duyệt trên bản sao
             Optional<SanPham> optSp = sanPhamRepo.findById(item.getSanPhamId());
             if(optSp.isPresent()){
                updateSanPhamStock(optSp.get(), item.getSoLuong()); // Cộng trả tồn kho
             }
        }
        items.clear();
        System.out.println("Giỏ hàng đã được xóa và tồn kho đã trả lại.");
    }

    // Hàm xóa giỏ hàng KHÔNG trả lại tồn kho (dùng sau khi thanh toán/lưu hóa đơn)
    public void clearCartWithoutRestock() {
        items.clear();
        System.out.println("Giỏ hàng đã được xóa sau khi thanh toán.");
    }

    // Tính tổng tiền
    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getThanhTien).sum();
    }

    // Hàm tiện ích tìm item trong giỏ theo ID sản phẩm
    private Optional<CartItem> findItemById(Long sanPhamId) {
        return items.stream()
                    .filter(item -> item.getSanPhamId() != null && item.getSanPhamId().equals(sanPhamId))
                    .findFirst();
    }

     // Hàm tiện ích cập nhật tồn kho
    private void updateSanPhamStock(SanPham sp, int quantityChange) {
        sp.setSoLuong(sp.getSoLuong() + quantityChange);
        sanPhamRepo.save(sp);
    }
}