package com.awbd.bookstore.exceptions.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super( message);
    }
}
