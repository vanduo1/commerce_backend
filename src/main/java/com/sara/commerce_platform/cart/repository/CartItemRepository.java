package com.sara.commerce_platform.cart.repository;

import com.sara.commerce_platform.cart.entity.Cart;
import com.sara.commerce_platform.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByProductIdAndCartId(String productId, String cartId);

}
