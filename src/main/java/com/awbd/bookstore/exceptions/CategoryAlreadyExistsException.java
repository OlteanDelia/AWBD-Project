package com.awbd.bookstore.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException() {
        super("A category with this name already exists.");
    }
}
