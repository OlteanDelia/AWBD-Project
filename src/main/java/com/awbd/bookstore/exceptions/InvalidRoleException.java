package com.awbd.bookstore.exceptions;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("The role is invalid!");
    }
}
