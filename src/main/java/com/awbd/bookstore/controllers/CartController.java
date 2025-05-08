package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.CartDTO;
import com.awbd.bookstore.exceptions.UserNotFoundException;
import com.awbd.bookstore.mappers.CartMapper;
import com.awbd.bookstore.models.Cart;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.services.CartService;
import com.awbd.bookstore.services.UserService;
import com.awbd.bookstore.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CartMapper cartMapper;

    @Autowired
    public CartController(CartService cartService, JwtUtil jwtUtil, UserService userService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.cartMapper = cartMapper;
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<CartDTO> addBookToCart(
            @PathVariable Long bookId,
            @RequestHeader("Authorization") String authHeader)  {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);


        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }



        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());


        Cart cart = cartService.getCartByUserId(user.getId());
        cartService.addBookToCart(cart.getId(), bookId);


        return ResponseEntity.ok(cartMapper.toDto(cart));
    }
    @GetMapping("/total")
    public ResponseEntity<Double> getTotalPrice(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);

        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        Cart cart = cartService.getCartByUserId(user.getId());
        double totalPrice = cartService.calculateTotalPrice(cart.getId());

        return ResponseEntity.ok(totalPrice);
    }
}




