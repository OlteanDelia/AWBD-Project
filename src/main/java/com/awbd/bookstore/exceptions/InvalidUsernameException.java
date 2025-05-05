package com.awbd.bookstore.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException() {
        super("The username is invalid!");
    }
}
