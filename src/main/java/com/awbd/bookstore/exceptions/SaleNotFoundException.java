package com.awbd.bookstore.exceptions;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException(Long id) {super("The sale with id" + id + "not found.");}
}
