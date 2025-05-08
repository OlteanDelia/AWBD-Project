package com.awbd.bookstore.exceptions;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException() {
        super("User already has a pending order.");
    }
}
