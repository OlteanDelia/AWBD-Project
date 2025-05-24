package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.BookDTO;
import com.awbd.bookstore.annotations.RequireAdmin;
import com.awbd.bookstore.mappers.BookMapper;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private BookService bookService;
    private BookMapper bookMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        logger.info("Fetched all books");
        return bookMapper.toDtoList(books);
    }

    @GetMapping("/search/{title}")
    public List<Book> searchBooks(@PathVariable String title) {
        List<Book> books = bookService.searchByTitle(title);
        logger.info("Searched books with title: {}", title);
        return books;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDTO> addBook(
            @RequestBody
            @Valid
            BookDTO bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookService.addBook(book);
        logger.info("Added new book: {}", bookDto.getTitle());
        return ResponseEntity.created(URI.create("/api/books/" + savedBook.getId()))
                .body(bookMapper.toDto(savedBook));
    }

    @GetMapping("/in-stock")
    public List<Book> getInStockBooks() {
        List<Book> books = bookService.getBooksInStock();
        logger.info("Fetched all books in stock");
        return books;
    }
}