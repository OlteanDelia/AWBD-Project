package com.awbd.bookstore.services;

import com.awbd.bookstore.DTOs.BookDTO;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public void updateBookStock(Long bookId, int quantity) {
        bookRepository.updateStock(bookId, quantity);
    }

    public List<Book> getBooksInStock() {
        return bookRepository.findByStockGreaterThan(0);
    }

    public List<Book> getBookBycategoryId(Long CategoryId) {
        return bookRepository.findByCategoryId(CategoryId);
    }

    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }

    public List<BookDTO> getBooksByAuthorId(Long authorId) {
        List<Book> books = bookRepository.findByAuthorId(authorId);
        return books.stream()
                .map(book -> new BookDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getPrice(),
                        book.getStock(),
                        book.getCategory().getId(),
                        book.getAuthor().getId(),
                        book.getAuthor().getName()
                ))
                .collect(Collectors.toList());
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }
}