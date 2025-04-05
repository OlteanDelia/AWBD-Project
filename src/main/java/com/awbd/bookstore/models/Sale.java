package com.awbd.bookstore.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales")
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


    public Sale() {
        this.saleCode = generateSaleCode();
    }

    public Sale(Double discountPercentage, LocalDateTime startDate, LocalDateTime endDate) {
        this.saleCode = generateSaleCode();
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private String generateSaleCode() {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "SALE-" + uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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