package com.awbd.bookstore.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException() {
        super("The requested category was not found.");
    }
}
