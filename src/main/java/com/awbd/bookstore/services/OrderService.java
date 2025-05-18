package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.book.BookNotFoundException;
import com.awbd.bookstore.exceptions.OrderNotFoundException;
import com.awbd.bookstore.exceptions.user.UserNotFoundException;
import com.awbd.bookstore.models.*;
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
    private final SaleService saleService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, BookRepository bookRepository, SaleService saleService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.saleService = saleService;
    }

    public Order createOrder(Long userId, List<Long> bookIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));


        Order order = new Order(user);

        bookIds.forEach(bookId -> {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));
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
                .orElseThrow(() -> new OrderNotFoundException());

        // Optional: update user
        if (userId != null && !userId.equals(order.getUser().getId())) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
            order.setUser(user);
        }

        // Update books
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
                .orElseThrow(() -> new OrderNotFoundException());
        order.setSale(saleService.getById(saleId));

        orderRepository.save(order);
    }

    // calculate total pric, considering the sales, if they exist
    public void getTotalPrice(Long oredrId){
        Order order = orderRepository.findById(oredrId)
                .orElseThrow(() -> new OrderNotFoundException());

        Sale sale = order.getSale();


        double totalPrice = 0.0;
        double percentage = sale.getDiscountPercentage();

        if(sale != null) {
            List<Category> sale_categ = sale.getCategories();
            for (Book book : order.getBooks()) {
                if (sale_categ.contains(book.getCategory())) {
                    totalPrice += book.getPrice() * (1 - percentage / 100);
                } else {
                    totalPrice += book.getPrice();
                }
            }
        }

        order.setTotalPrice(totalPrice);
    }





}