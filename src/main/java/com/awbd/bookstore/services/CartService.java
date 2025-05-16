package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.BookNotFoundException;
import com.awbd.bookstore.exceptions.CartNotFoundException;
import com.awbd.bookstore.exceptions.OutOfStockException;
import com.awbd.bookstore.exceptions.UserNotFoundException;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.models.Cart;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.repositories.BookRepository;
import com.awbd.bookstore.repositories.CartRepository;
import com.awbd.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SaleService saleService;

    @Autowired
    public CartService(CartRepository cartRepository, BookRepository bookRepository, UserRepository userRepository, SaleService saleService) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.saleService = saleService;
    }

    public Cart getCartByUserId(Long userId){
        return cartRepository.findByUserId(userId);
    }

    public Cart createCart(Long userId){
        if(cartRepository.existsByUserId(userId)){
            throw new IllegalStateException("Cart already exists for this user");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        Cart cart = new Cart(user);
        return cartRepository.save(cart);
    }

    public void addBookToCart(Long cartId, Long bookId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException());

        if(cartRepository.existsBookInCart(cartId, bookId)){
            throw new IllegalStateException("Book already in Cart");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException());

        if(book.getStock() <= 0){
            throw new OutOfStockException("Book is out of stock.");
        }
        cart.addBook(book);
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long cartId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException());
        cart.getBooks().clear(); // sterge toate cartile din cos
        cartRepository.save(cart);
    }

    public double calculateTotalPrice(Long cartId){
        return cartRepository.calculateTotalPrice(cartId);
    }

    // apply sale to cart to the books from the category of the sale
}
