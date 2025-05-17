package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.BookNotFoundException;
import com.awbd.bookstore.exceptions.OrderNotFoundException;
import com.awbd.bookstore.exceptions.UserNotFoundException;
import com.awbd.bookstore.models.*;
import com.awbd.bookstore.repositories.BookRepository;
import com.awbd.bookstore.repositories.OrderRepository;
import com.awbd.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public void applySaleToOrder(Long orderId, Long saleId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());
        order.setSale(saleService.getSaleById(saleId));

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