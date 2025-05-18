package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.BookDTO;
import com.awbd.bookstore.annotations.RequireAdmin;
import com.awbd.bookstore.mappers.BookMapper;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        logger.info("Fetched all books");
        return ResponseEntity.ok(bookMapper.toDtoList(books));
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<List<BookDTO>> searchBooks(@PathVariable String title) {
        List<Book> books = bookService.searchByTitle(title);
        logger.info("Searched books with title: {}", title);
        return ResponseEntity.ok(bookMapper.toDtoList(books));
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDto) {

        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookService.addBook(book);
        logger.info("Added new book: {}", bookDto.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookMapper.toDto(savedBook));
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<BookDTO>> getInStockBooks() {
        List<Book> books = bookService.getBooksInStock();
        logger.info("Fetched all books in stock");
        return ResponseEntity.ok(bookMapper.toDtoList(books));
    }



}
