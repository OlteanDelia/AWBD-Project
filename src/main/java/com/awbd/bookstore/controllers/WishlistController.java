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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {
    private final WishlistService wishlistService;
    private final WishlistMapper wishlistMapper;
    private final BookMapper bookMapper;
    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);

    @Autowired
    public WishlistController(WishlistService wishlistService, WishlistMapper wishlistMapper, BookMapper bookMapper) {
        this.wishlistService = wishlistService;
        this.wishlistMapper = wishlistMapper;
        this.bookMapper = bookMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<BookDTO>> getBooksByWishlistId(@PathVariable Long id) {
        List<Book> books = wishlistService.getBooksByWishlistId(id);
        if (books.isEmpty()) {
            logger.warn("No books found in wishlist with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Books found in wishlist with id: {}", id);
        return ResponseEntity.ok(bookMapper.toDtoList(books));
    }

    @PostMapping("/{id}/{bookId}")
    public ResponseEntity<Void> addBookToWishlist(@PathVariable Long id, @PathVariable Long bookId) {
        wishlistService.addBookToWishlist(id, bookId);
        logger.info("Book with id: {} added to wishlist with id: {}", bookId, id);
        return ResponseEntity.ok().build();
    }


}
