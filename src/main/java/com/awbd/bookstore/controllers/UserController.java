package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.LoginRequestDTO;
import com.awbd.bookstore.DTOs.LoginResponseDTO;
import com.awbd.bookstore.DTOs.RegisterRequestDTO;
import com.awbd.bookstore.DTOs.UserDTO;
import com.awbd.bookstore.annotations.RequireAdmin;
import com.awbd.bookstore.exceptions.UserNotFoundException;
import com.awbd.bookstore.mappers.UserMapper;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.services.UserService;
import com.awbd.bookstore.utils.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        System.out.println("DEBUG: Endpoint accesat cu succes!");
        System.out.println("DEBUG: Request primit: " + registerRequest);

        try {
            System.out.println("DEBUG: Username din request: " + registerRequest.getUsername());
            System.out.println("DEBUG: Password din request: " + registerRequest.getPassword());
            System.out.println("DEBUG: Role din request: " + registerRequest.getRole());

            User newUser = new User();
            System.out.println("DEBUG: User creat");

            newUser.setUsername(registerRequest.getUsername());
            System.out.println("DEBUG: Username setat: " + newUser.getUsername());

            newUser.setPassword(registerRequest.getPassword());
            System.out.println("DEBUG: Password setat");

            try {
                System.out.println("DEBUG: Încercăm să convertim rolul: " + registerRequest.getRole());
                User.Role role = User.Role.valueOf(registerRequest.getRole());
                System.out.println("DEBUG: Rol convertit cu succes: " + role);
                newUser.setRole(role);
                System.out.println("DEBUG: Rol setat în user");
            } catch (IllegalArgumentException e) {
                System.out.println("DEBUG: EROARE la convertirea rolului: " + e.getMessage());
                return ResponseEntity.badRequest().body(null);
            }

            System.out.println("DEBUG: Încercăm să creăm userul în baza de date");
            User createdUser = userService.create(newUser);
            System.out.println("DEBUG: User creat în baza de date cu ID: " + createdUser.getId());

            System.out.println("DEBUG: Convertim user la DTO");
            UserDTO dto = userMapper.toDto(createdUser);
            System.out.println("DEBUG: DTO creat cu succes");

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println("DEBUG: EROARE GENERALĂ: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> userOptional = userService.findByUsername(loginRequest.getUsername());



        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPassword = Base64.getEncoder()
                    .encodeToString(loginRequest.getPassword().getBytes());


            if (encodedPassword.equals(user.getPassword())) {
                logger.info("LOG: Authorized");
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                logger.info("LOG: Token: " + token);


                LoginResponseDTO response = new LoginResponseDTO(
                        userMapper.toDto(user),
                        token
                );

                logger.info("Token" + token);

                return ResponseEntity.ok(response);
            }
        }

        logger.info("LOG: Unauthorized");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        long idLong = Long.parseLong(id);
        return userService.findById(idLong)
                .map(user -> ResponseEntity.ok(userMapper.toDto(user)))
                .orElseThrow(() -> new UserNotFoundException());

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO userDto) {

        Long idLong = Long.parseLong(id);
        User updatedUser = userService.update(idLong, userMapper.toEntity(userDto));
        logger.info("Update for user with ID " + idLong);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {

        userService.delete(id);
        Map<String, String> response = new HashMap<>();
        logger.info("User with ID " + id + " was deleted");

        return ResponseEntity.ok(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        logger.info("Logout");
        return ResponseEntity.ok().build();

    }

}


