package com.awbd.bookstore.exceptions.order;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message ) {
        super(message);
    }
}
