package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.BookNotFoundException;
import com.awbd.bookstore.exceptions.OrderNotFoundException;
import com.awbd.bookstore.exceptions.UserNotFoundException;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.models.Order;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.repositories.BookRepository;
import com.awbd.bookstore.repositories.OrderRepository;
import com.awbd.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Order createOrder(Long userId, List<Long> bookIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());


        Order order = new Order(user);

        bookIds.forEach(bookId -> {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException());
            order.addBook(book);
        });

        return orderRepository.save(order);
    }

    public List<Order> getUserOrderHistory(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }


    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());
    }

    public Order updateOrder(Long orderId, Long userId, Set<Long> bookIds) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        // Optional: update user
        if (userId != null && !userId.equals(order.getUser().getId())) {
            User user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);
            order.setUser(user);
        }

        // Update books
        Set<Book> books = new HashSet<>();
        for (Long bookId : bookIds) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(BookNotFoundException::new);
            books.add(book);
        }
        order.setBooks(books);

        return orderRepository.save(order);
    }



}