package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.CategoryAlreadyExistsException;
import com.awbd.bookstore.exceptions.CategoryNotFoundException;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.models.Category;
import com.awbd.bookstore.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new CategoryAlreadyExistsException();
        }
        return categoryRepository.save(category);
    }



    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException());
    }

    public List<Book> getBooksInCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        return category.getBooks();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

}
