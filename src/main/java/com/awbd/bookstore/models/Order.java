package com.awbd.bookstore.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")  // order este cuv rezervat in SQL
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToMany
    @JoinTable(
            name = "order_book",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    private LocalDateTime orderDate;


    public void setId(long id) {
        this.id = id;
    }



    public Order() {
        this.orderDate = LocalDateTime.now();

    }


    public Order(User user) {
        this();  // apeleaza constructorul gol pentru a seta data
        this.user = user;
    }


    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }



    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }



    public void addBook(Book book) {
        this.books.add(book);

    }

    public void removeBook(Book book) {
        this.books.remove(book);

    }


}