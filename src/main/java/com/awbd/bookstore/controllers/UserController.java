package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.LoginRequestDTO;
import com.awbd.bookstore.DTOs.LoginResponseDTO;
import com.awbd.bookstore.DTOs.RegisterRequestDTO;
import com.awbd.bookstore.DTOs.UserDTO;
import com.awbd.bookstore.annotations.RequireAdmin;
import com.awbd.bookstore.exceptions.user.InvalidRoleException;
import com.awbd.bookstore.exceptions.user.UserNotFoundException;
import com.awbd.bookstore.mappers.UserMapper;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.services.UserService;
import com.awbd.bookstore.utils.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;



@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    private UserMapper userMapper;
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, UserMapper userMapper, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(
            @RequestBody
            @Valid
            RegisterRequestDTO registerRequest) {

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());

        try {
            User.Role role = User.Role.valueOf(registerRequest.getRole());
            newUser.setRole(role);
        } catch (IllegalArgumentException e) {
            logger.error("Error at role conversion: {}", e.getMessage());
            throw new InvalidRoleException("Invalid role: " + registerRequest.getRole());
        }

        User createdUser = userService.create(newUser);
        logger.info("User created with id {}", createdUser.getId());

        return ResponseEntity.created(URI.create("/api/users/" + createdUser.getId()))
                .body(userMapper.toDto(createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> userOptional = userService.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPassword = Base64.getEncoder()
                    .encodeToString(loginRequest.getPassword().getBytes());

            if (encodedPassword.equals(user.getPassword())) {
                logger.info("Authorized: user {}", user.getUsername());
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                logger.info("Token generated for user {}", user.getUsername());

                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("token", token);

                return ResponseEntity.ok(response);
            }
        }

        logger.info("Unauthorized login attempt for username: {}", loginRequest.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));

        logger.info("Retrieved user with ID: {}", id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable
            Long id,

            @RequestBody
            @Valid
            UserDTO userDto) {

        if (userDto.getId() != null && !id.equals(userDto.getId())) {
            logger.warn("ID mismatch: path ID {} doesn't match body ID {}", id, userDto.getId());
            throw new RuntimeException("Id from path does not match with id from request");
        }

        User user = userMapper.toEntity(userDto);
        User updatedUser = userService.update(id, user);
        logger.info("Updated user with ID {}", id);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        logger.info("User with ID {} was deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        logger.info("Logout successful");
        return ResponseEntity.ok().build();
    }
}