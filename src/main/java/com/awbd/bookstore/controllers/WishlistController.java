package com.awbd.bookstore.controllers;


import com.awbd.bookstore.DTOs.BookDTO;
import com.awbd.bookstore.mappers.BookMapper;
import com.awbd.bookstore.mappers.WishlistMapper;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {
    private final WishlistService wishlistService;
    private final WishlistMapper wishlistMapper;
    private final BookMapper bookMapper;

    @Autowired
    public WishlistController(WishlistService wishlistService, WishlistMapper wishlistMapper, BookMapper bookMapper) {
        this.wishlistService = wishlistService;
        this.wishlistMapper = wishlistMapper;
        this.bookMapper = bookMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<BookDTO>> getBooksByWishlistId(@PathVariable Long id) {
        List<Book> books = wishlistService.getBooksByWishlistId(id);
        return ResponseEntity.ok(bookMapper.toDtoList(books));
    }

    @PostMapping("/{id}/{bookId}")
    public ResponseEntity<Void> addBookToWishlist(@PathVariable Long id, @PathVariable Long bookId) {
        wishlistService.addBookToWishlist(id, bookId);
        return ResponseEntity.ok().build();
    }


}
