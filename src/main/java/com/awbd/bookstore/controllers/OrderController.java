package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.OrderRequestDTO;
import com.awbd.bookstore.DTOs.OrderDTO;
import com.awbd.bookstore.DTOs.OrderRequestDTO;
import com.awbd.bookstore.annotations.RequireAdmin;
import com.awbd.bookstore.exceptions.user.UserNotFoundException;
import com.awbd.bookstore.mappers.OrderMapper;
import com.awbd.bookstore.models.*;
import com.awbd.bookstore.services.CartService;
import com.awbd.bookstore.services.OrderService;
import com.awbd.bookstore.services.UserService;
import com.awbd.bookstore.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


    @Autowired
    public OrderController(OrderService orderService,
                           JwtUtil jwtUtil,
                           UserService userService,
                           CartService cartService,
                           OrderMapper orderMapper) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
    }


    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) OrderRequestDTO request) {

        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Cart cart = cartService.getCartByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getId()));
        if (cart.getBooks().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        List<Long> bookIds = cart.getBooks().stream()
                .map(Book::getId)
                .collect(Collectors.toList());


        Long saleId = (request != null) ? request.getSaleId() : null;
        Order order = orderService.createOrder(user.getId(), bookIds, saleId);

        cartService.clearCart(cart.getId());
        logger.info("Order created successfully for user: {}", username);

        return ResponseEntity.ok(orderMapper.toDto(order));
    }
    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getUserOrderHistory(
            @RequestHeader("Authorization") String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        List<Order> orders = orderService.getUserOrderHistory(user);
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        logger.info("Order history retrieved successfully for user: {}", username);
        return ResponseEntity.ok(orderMapper.toDtoList(orders));
    }


    @GetMapping
    @RequireAdmin
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        logger.info("All orders retrieved successfully");
        return ResponseEntity.ok(orderMapper.toDtoList(orders));
    }

    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        Order updatedOrder = orderService.updateOrder(id, orderDTO.getUserId(), orderDTO.getBookIds());
        OrderDTO updatedDTO = orderMapper.toDto(updatedOrder);
        logger.info("Order with ID {} updated successfully", id);
        return ResponseEntity.ok(updatedDTO);
    }

}