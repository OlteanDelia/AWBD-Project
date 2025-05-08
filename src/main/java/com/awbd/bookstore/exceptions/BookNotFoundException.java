package com.awbd.bookstore.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException() {
        super("The requested book was not found.");
    }
}
