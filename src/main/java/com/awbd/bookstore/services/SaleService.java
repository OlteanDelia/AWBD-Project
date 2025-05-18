package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.order.SaleNotFoundException;
import com.awbd.bookstore.exceptions.category.CategoryNotFoundException;
import com.awbd.bookstore.models.Category;
import com.awbd.bookstore.models.Sale;
import com.awbd.bookstore.repositories.CategoryRepository;
import com.awbd.bookstore.repositories.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    private SaleRepository saleRepository;
    private CategoryRepository categoryRepository;

    public SaleService(SaleRepository saleRepository, CategoryRepository categoryRepository) {
        this.saleRepository = saleRepository;
        this.categoryRepository = categoryRepository;
    }

    public Sale create(Sale sale, List<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(categoryIds);

            if (categories.size() != categoryIds.size()) {
                throw new CategoryNotFoundException("One or more categories not found");
            }

            sale.setCategories(categories);
        }

        return saleRepository.save(sale);
    }

    public Sale getById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale with ID " + id + " not found"));
    }

    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    public Sale update(Long id, Sale updatedSale, List<Long> categoryIds) {
        return saleRepository.findById(id)
                .map(existingSale -> {
                    existingSale.setDiscountPercentage(updatedSale.getDiscountPercentage());
                    existingSale.setStartDate(updatedSale.getStartDate());
                    existingSale.setEndDate(updatedSale.getEndDate());
                    existingSale.setDescription(updatedSale.getDescription());
                    existingSale.setIsActive(updatedSale.getIsActive());

                    if (categoryIds != null && !categoryIds.isEmpty()) {
                        List<Category> categories = categoryRepository.findAllById(categoryIds);

                        if (categories.size() != categoryIds.size()) {
                            throw new CategoryNotFoundException("One or more categories not found");
                        }

                        existingSale.setCategories(categories);
                    }

                    return saleRepository.save(existingSale);
                })
                .orElseThrow(() -> new SaleNotFoundException("Sale with ID " + id + " not found"));
    }

    public void delete(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new SaleNotFoundException("Sale with ID " + id + " not found");
        }
        saleRepository.deleteById(id);
    }

    public List<Sale> getAllActiveSales() {
        return saleRepository.findAllActiveSales();
    }
}