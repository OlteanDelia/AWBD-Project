package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.OrderDTO;
import com.awbd.bookstore.annotations.RequireAdmin;
import com.awbd.bookstore.exceptions.UserNotFoundException;
import com.awbd.bookstore.mappers.OrderMapper;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.models.Cart;
import com.awbd.bookstore.models.Order;
import com.awbd.bookstore.models.User;
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

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CartService cartService;
    private final OrderMapper orderMapper;

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
    public ResponseEntity<OrderDTO> createOrder(@RequestHeader("Authorization") String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());


        Cart cart = cartService.getCartByUserId(user.getId());
        if (cart.getBooks().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }


        List<Long> bookIds = cart.getBooks().stream()
                .map(Book::getId)
                .collect(Collectors.toList());

        Order order = orderService.createOrder(user.getId(), bookIds);


        cartService.clearCart(cart.getId());

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
                .orElseThrow(() -> new UserNotFoundException());

        List<Order> orders = orderService.getUserOrderHistory(user);
        return ResponseEntity.ok(orderMapper.toDtoList(orders));
    }


    @GetMapping
    @RequireAdmin
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orderMapper.toDtoList(orders));
    }
}