package com.awbd.bookstore.services;


import com.awbd.bookstore.models.Wishlist;
import com.awbd.bookstore.repositories.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.awbd.bookstore.models.Book;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<Book> getBooksByWishlistId(Long wishlistId) {
        return wishlistRepository.findBooksByWishlistId(wishlistId);
    }

    public Book addBookToWishlist(Long wishlistId, Long bookId ){
        Optional<Wishlist> wishlistOptional = wishlistRepository.findById(wishlistId);
        if (wishlistOptional.isPresent()) {
            Wishlist wishlist = wishlistOptional.get();
            Set<Book> books = wishlist.getBooks();
            for (Book book : books) {
                if (book.getId() == bookId) {
                    throw new RuntimeException("Book already exists in the wishlist");
                }
            }
            Book newBook = new Book();
            newBook.setId(bookId);
            books.add(newBook);
            wishlist.setBooks(books);
            wishlistRepository.save(wishlist);
            return newBook;
        } else {
            throw new RuntimeException("Wishlist not found");
        }
    }
}
