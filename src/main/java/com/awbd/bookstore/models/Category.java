package com.awbd.bookstore.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
        @Id
        @GeneratedValue
        private long id;
        private String name;
        private String description;
        @OneToMany(mappedBy = "category")
        private List<Book> books;
        @ManyToMany(mappedBy = "categories")
        private List<Sale> sales = new ArrayList<>();

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {this.id = id; }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Sale> getSales() {
        return sales;
    }
}
