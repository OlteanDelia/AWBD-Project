package com.awbd.bookstore.exceptions;

public class DuplicateUserException extends RuntimeException{
    public DuplicateUserException() {
        super("A user with the same username already exists.");
    }
}
