package com.awbd.bookstore.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("The requested user was not found.");
    }
}
