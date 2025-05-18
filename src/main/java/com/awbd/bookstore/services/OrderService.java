package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.book.BookNotFoundException;
import com.awbd.bookstore.exceptions.order.OrderNotFoundException;
import com.awbd.bookstore.exceptions.user.UserNotFoundException;
import com.awbd.bookstore.exceptions.cart.EmptyCartException;
import com.awbd.bookstore.exceptions.order.SaleNotFoundException;
import com.awbd.bookstore.models.*;
import com.awbd.bookstore.repositories.BookRepository;
import com.awbd.bookstore.repositories.OrderRepository;
import com.awbd.bookstore.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private SaleService saleService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        BookRepository bookRepository, SaleService saleService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.saleService = saleService;
    }

    public Order createOrder(Long userId, List<Long> bookIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        if (bookIds.isEmpty()) {
            throw new EmptyCartException("Cannot create order with empty book list");
        }

        Order order = new Order(user);

        bookIds.forEach(bookId -> {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));
            order.addBook(book);
        });

        return orderRepository.save(order);
    }

    public List<Order> getUserOrderHistory(User user) {
        if (user == null) {
            throw new UserNotFoundException("User cannot be null");
        }
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));
    }

    public Order updateOrder(Long orderId, Long userId, Set<Long> bookIds) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));

        // Optional: update user
        if (userId != null && !userId.equals(order.getUser().getId())) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
            order.setUser(user);
        }

        // Update books
        if (bookIds == null || bookIds.isEmpty()) {
            throw new EmptyCartException("Book IDs cannot be empty");
        }

        Set<Book> books = new HashSet<>();
        for (Long bookId : bookIds) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));
            books.add(book);
        }
        order.setBooks(books);

        return orderRepository.save(order);
    }

    public void applySaleToOrder(Long orderId, Long saleId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));

        Sale sale = saleService.getById(saleId);
        if (sale == null) {
            throw new SaleNotFoundException("Sale with ID " + saleId + " not found");
        }

        order.setSale(sale);
        orderRepository.save(order);
    }

    // calculate total price, considering the sales, if they exist
    public void getTotalPrice(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));

        Sale sale = order.getSale();
        double totalPrice = 0.0;

        if (sale != null) {
            double percentage = sale.getDiscountPercentage();
            List<Category> sale_categ = sale.getCategories();

            for (Book book : order.getBooks()) {
                if (sale_categ.contains(book.getCategory())) {
                    totalPrice += book.getPrice() * (1 - percentage / 100);
                } else {
                    totalPrice += book.getPrice();
                }
            }
        } else {
            // Calculate total without discount
            for (Book book : order.getBooks()) {
                totalPrice += book.getPrice();
            }
        }

        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
    }
}