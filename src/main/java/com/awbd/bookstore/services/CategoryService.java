package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.*;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.models.Category;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
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

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException();
        }
        categoryRepository.deleteById(id);
    }

    public Category update(Long id, Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Verifică dacă noua categorie există deja la alta cat
                    if (!existingCategory.getName().equals(updatedCategory.getName()) &&
                            categoryRepository.existsByName(updatedCategory.getName())) {
                        throw new DuplicateCategoryException();
                    }


                    existingCategory.setName(updatedCategory.getName());
                    existingCategory.setDescription(updatedCategory.getDescription());

                    return categoryRepository.save(existingCategory);
                })
                .orElseThrow(() -> new CategoryNotFoundException());
    }




}
