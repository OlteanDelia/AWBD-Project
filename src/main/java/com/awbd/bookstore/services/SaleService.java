package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.SaleNotFoundException;
import com.awbd.bookstore.models.Category;
import com.awbd.bookstore.models.Sale;
import com.awbd.bookstore.repositories.CategoryRepository;
import com.awbd.bookstore.repositories.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CategoryRepository categoryRepository;

    public SaleService(SaleRepository saleRepository, CategoryRepository categoryRepository) {
        this.saleRepository = saleRepository;
        this.categoryRepository = categoryRepository;
    }

    public Sale create(Sale sale, List<Long> categoryIds) {
        if (categoryIds != null) {
            List<Category> categories = categoryRepository.findAllById(categoryIds);
            sale.setCategories(categories);
        }
        return saleRepository.save(sale);
    }

    public Sale getById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException(id));
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

                    if (categoryIds != null) {
                        List<Category> categories = categoryRepository.findAllById(categoryIds);
                        existingSale.setCategories(categories);
                    }

                    return saleRepository.save(existingSale);
                })
                .orElseThrow(() -> new SaleNotFoundException(id));
    }

    public void delete(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new SaleNotFoundException(id);
        }
        saleRepository.deleteById(id);
    }
}
