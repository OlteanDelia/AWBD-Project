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

        logger.info("=== ORDER CREATION STARTED ===");

        // Validate auth header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Invalid authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extract username from token
        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.error("Invalid token - could not extract username");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info("Username from token: {}", username);

        // Find user
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        logger.info("User found: {} (ID: {})", user.getUsername(), user.getId());

        // Find cart
        Cart cart = cartService.getCartByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getId()));
        logger.info("Cart found: ID {}", cart.getId());

        // Get books in cart
        List<Book> booksInCart = cartService.getBooksInCart(cart.getId());
        logger.info("Books in cart: {}", booksInCart.size());

        if (booksInCart.isEmpty()) {
            logger.error("Cart is empty for user: {}", username);
            return ResponseEntity.badRequest().build();
        }

        // Extract book IDs
        List<Long> bookIds = booksInCart.stream()
                .map(Book::getId)
                .collect(Collectors.toList());
        logger.info("Book IDs: {}", bookIds);

        // Get sale ID
        Long saleId = null;
        if (request != null && request.getSaleId() != null) {
            saleId = request.getSaleId();
            logger.info("Sale ID from request: {}", saleId);
        } else {
            logger.info("No sale ID in request, proceeding without sale");
        }
        logger.info("Sale ID: {}", saleId);

        try {
            // Create order
            logger.info("About to call orderService.createOrder with userId: {}, bookIds: {}, saleId: {}",
                    user.getId(), bookIds, saleId);
            Order order = orderService.createOrder(user.getId(), bookIds, saleId);
            logger.info("Order created successfully: ID {}", order.getId());

            // Clear cart
            logger.info("About to clear cart with ID: {}", cart.getId());
            cartService.clearCart(cart.getId());
            logger.info("Cart cleared for user: {}", username);

            // Return order DTO
            logger.info("About to map order to DTO");
            OrderDTO orderDTO = orderMapper.toDto(order);
            logger.info("Order creation completed successfully");
            return ResponseEntity.ok(orderDTO);

        } catch (Exception e) {
            logger.error("=== DETAILED ERROR INFO ===");
            logger.error("Error class: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Error cause: {}", e.getCause() != null ? e.getCause().toString() : "No cause");
            logger.error("Full stack trace:", e);

            // Re-throw the exception to let global exception handler deal with it
            // Or return a generic error response
            throw new RuntimeException("Order creation failed: " + e.getMessage(), e);
        }
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