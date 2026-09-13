package com.sara.commerce_platform.order;

import com.sara.commerce_platform.order.dto.OrderRequest;
import com.sara.commerce_platform.order.dto.OrderResponse;
import com.sara.commerce_platform.user.entity.User;
import com.sara.commerce_platform.user.entity.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public ResponseEntity<List<OrderResponse>> getALLOrder(@AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = principal.getUser();

        return ResponseEntity.ok(orderService.getAllOrder(currentUser.getId()));
    }



    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId,
    @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = principal.getUser();

        return ResponseEntity.ok(orderService.getOrderById(orderId, currentUser.getId()));
    }


    @PostMapping("")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody OrderRequest request
    ) {
        User currentUser = principal.getUser();
        return ResponseEntity.ok(orderService.checkout(currentUser.getId(), request));
    }
}
