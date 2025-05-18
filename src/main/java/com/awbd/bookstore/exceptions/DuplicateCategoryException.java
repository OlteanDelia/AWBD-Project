package com.awbd.bookstore.exceptions;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException() {super("A category with the same name already exists");}
}
