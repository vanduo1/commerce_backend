package com.sara.commerce_platform.order;

import com.sara.commerce_platform.cart.entity.Cart;
import com.sara.commerce_platform.cart.entity.CartItem;
import com.sara.commerce_platform.cart.repository.CartItemRepository;
import com.sara.commerce_platform.cart.repository.CartRepository;
import com.sara.commerce_platform.common.exception.ProductNotFoundException;
import com.sara.commerce_platform.order.dto.OrderRequest;
import com.sara.commerce_platform.order.dto.OrderResponse;
import com.sara.commerce_platform.order.entity.Order;
import com.sara.commerce_platform.order.entity.OrderItem;
import com.sara.commerce_platform.order.repository.OrderRepository;
import com.sara.commerce_platform.product.Product;
import com.sara.commerce_platform.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderMapper orderMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponse> getAllOrder(String userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);

        return orders.stream().map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId, String userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderMapper.toResponse(order);
    }


    @Transactional
    public OrderResponse checkout(String userId, OrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> items = cart.getItems();

        if(items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        Order order = Order.builder()
                .userId(userId)
                .build();


        for(String itemId : request.cartItemIds()) {
            CartItem item = cartItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            Product product = productRepository.findById(item.getProductId()).orElseThrow(
                    () -> new ProductNotFoundException(item.getProductId()));

            if(item.getQuantity() <= product.getStock()) {
                OrderItem orderItem = OrderItem.builder()
                        .productId(product.getId())
                        .order(order)
                        .price(product.getPrice())
                        .quantity(item.getQuantity())
                        .build();

                order.addOrderItem(orderItem);
                product.setStock(product.getStock() - item.getQuantity());
                productRepository.save(product);

                order.setTotalPrice(order.getTotalPrice().add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
                cart.removeItem(item);
            } else {
                throw new RuntimeException("Product quantity is not enough: " + item.getQuantity() + ". Stock: " + product.getStock());
            }
        }
        orderRepository.save(order);
        return  orderMapper.toResponse(order);
    }
}
