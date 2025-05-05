package com.awbd.bookstore.exceptions;

public class CartNotFoundException  extends RuntimeException{
    public CartNotFoundException() {
        super("The requested cart was not found.");
    }
}
