package com.awbd.bookstore.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("The requested order was not found.");
    }
}
