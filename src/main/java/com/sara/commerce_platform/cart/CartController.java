package com.sara.commerce_platform.cart;

import com.sara.commerce_platform.cart.dto.CartItemRequest;
import com.sara.commerce_platform.cart.dto.CartResponse;
import com.sara.commerce_platform.user.entity.User;
import com.sara.commerce_platform.user.entity.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("")
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User currentUser = userPrincipal.getUser();


        return ResponseEntity.ok( cartService.getCart(currentUser.getId()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CartItemRequest request) {
        User currentUser = userPrincipal.getUser();

        return ResponseEntity.ok(cartService.addToCart(currentUser.getId(), request));
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String itemId
    ) {
        User currentUser = userPrincipal.getUser();
        return ResponseEntity.ok(cartService.removeCartItem(currentUser.getId(), itemId));
    }
}
