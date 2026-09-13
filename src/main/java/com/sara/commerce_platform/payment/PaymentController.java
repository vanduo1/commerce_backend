package com.sara.commerce_platform.payment;

import com.sara.commerce_platform.order.dto.OrderResponse;
import com.sara.commerce_platform.order.entity.Order;
import com.sara.commerce_platform.order.repository.OrderRepository;
import com.sara.commerce_platform.payment.dto.MomoIpnRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final MomoService momoService;
    private final OrderRepository orderRepository;

    public PaymentController(MomoService momoService, OrderRepository orderRepository) {
        this.momoService = momoService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/momo/create/{orderId}")
    public ResponseEntity<String> createPaymentUrl(
            @PathVariable String orderId
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return  ResponseEntity.ok(momoService.createPaymentUrl(order));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleMomoIpn(@RequestBody MomoIpnRequest ipnData) {
        momoService.processIpn(ipnData);
        return ResponseEntity.ok().build();
    }

}
