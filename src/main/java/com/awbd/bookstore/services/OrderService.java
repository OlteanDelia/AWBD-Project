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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public Order createOrder(Long userId, List<Long> bookIds, Long saleId) {
        logger.info("Creating order for user: {}, books: {}, sale: {}", userId, bookIds, saleId);

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

        Sale sale = null;

        if (saleId != null) {
            try {
                sale = saleService.getById(saleId);
                if (sale != null && sale.getIsActive()) {
                    order.setSale(sale);
                    logger.info("Applied sale: {} to order", sale.getSaleCode());
                } else {
                    logger.warn("Sale with ID {} is not active, proceeding without discount", saleId);
                    sale = null;
                }
            } catch (SaleNotFoundException e) {
                logger.warn("Sale with ID {} not found, proceeding without discount", saleId);
                sale = null;
            }
        } else {
            logger.info("No sale ID provided, creating order without discount");
        }

        double totalPrice = 0.0;

        if (sale == null) {
            totalPrice = order.getBooks().stream()
                    .mapToDouble(Book::getPrice)
                    .sum();
            logger.info("Order total without discount: ${}", totalPrice);
        } else {
            double percentage = sale.getDiscountPercentage();
            List<Category> saleCategories = sale.getCategories();

            for (Book book : order.getBooks()) {
                if (saleCategories.contains(book.getCategory())) {
                    totalPrice += book.getPrice() * (1 - percentage / 100);
                    logger.debug("Applied {}% discount to book: {}", percentage, book.getTitle());
                } else {
                    totalPrice += book.getPrice();
                }
            }
            logger.info("Order total with discount: ${}", totalPrice);
        }

        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with ID: {}", savedOrder.getId());

        if (sale != null) {
            logger.info("Deactivating sale: {}", sale.getSaleCode());
            sale.setIsActive(false);
            saleService.update(sale.getId(), sale, null);
            logger.info("Sale {} has been deactivated", sale.getSaleCode());
        }

        return savedOrder;
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

        if (userId != null && !userId.equals(order.getUser().getId())) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
            order.setUser(user);
        }

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

    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found");
        }
        orderRepository.deleteById(orderId);
        logger.info("Order with ID {} deleted successfully", orderId);
    }


}