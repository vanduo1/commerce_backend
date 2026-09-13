package com.sara.commerce_platform.cart;

import com.sara.commerce_platform.cart.dto.CartItemRequest;
import com.sara.commerce_platform.cart.dto.CartResponse;
import com.sara.commerce_platform.cart.entity.Cart;
import com.sara.commerce_platform.cart.entity.CartItem;
import com.sara.commerce_platform.cart.repository.CartItemRepository;
import com.sara.commerce_platform.cart.repository.CartRepository;
import com.sara.commerce_platform.common.exception.ProductNotFoundException;
import com.sara.commerce_platform.product.Product;
import com.sara.commerce_platform.product.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CartService {
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartMapper cartMapper, ProductRepository productRepository, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartMapper = cartMapper;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet( () ->  cartRepository.save(Cart.builder()
                        .userId(userId)
                        .build()));

        return cartMapper.toResponse(cart);
    }




    @Transactional
    public CartResponse addToCart(String userId, CartItemRequest request) {
        Product product = productRepository.findById(request.productId()).orElseThrow(
                () -> new ProductNotFoundException(request.productId()));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet( () ->  cartRepository.save(Cart.builder()
                                .userId(userId)
                        .build()));


        CartItem item = cartItemRepository.findByProductIdAndCartId(product.getId(), cart.getId())
                .orElseGet(() -> CartItem.builder()
                        .productId(product.getId())
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + request.quantity());
        cart.addItem(item);
        cartRepository.save(cart);

        return cartMapper.toResponse(cart);
    }

    @Transactional

    public CartResponse removeCartItem(String userId, String itemId) {
        log.info("Removing item {} from cart of user {}", itemId, userId);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));

        log.info("Finding item {} from cart {}", itemId, cart.getId());
        CartItem item =   cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        cart.removeItem(item);
        cartRepository.save(cart);


        return cartMapper.toResponse(cart);
    }



}
