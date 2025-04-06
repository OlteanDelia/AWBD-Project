package com.awbd.bookstore.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String saleCode;

    @Column(nullable = false)
    private Double discountPercentage;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private String description;

    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "sale_category",
            joinColumns = @JoinColumn(name = "sale_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    {
        this.saleCode = generateSaleCode();
    }

    public Sale(Double discountPercentage, LocalDateTime startDate, LocalDateTime endDate) {
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private String generateSaleCode() {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();// create a random UUID, we take only the first 8 characters
        return "SALE-" + uuid;
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.getSales().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getSales().remove(this);
    }

    public boolean isValidAt(LocalDateTime dateTime) {
        return isActive &&
                dateTime.isAfter(startDate) &&
                dateTime.isBefore(endDate);
    }
}