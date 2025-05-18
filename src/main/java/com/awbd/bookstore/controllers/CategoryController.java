package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.BookDTO;
import com.awbd.bookstore.DTOs.CategoryDTO;
import com.awbd.bookstore.DTOs.UserDTO;
import com.awbd.bookstore.annotations.RequireAdmin;
import com.awbd.bookstore.mappers.BookMapper;
import com.awbd.bookstore.mappers.CategoryMapper;
import com.awbd.bookstore.models.Book;
import com.awbd.bookstore.models.Category;
import com.awbd.bookstore.models.User;
import com.awbd.bookstore.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper, BookMapper bookMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.bookMapper = bookMapper;
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedcategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryMapper.toDto(savedcategory));
    }

    // get all books from a category
    @GetMapping("/{id}")
    public ResponseEntity<List<BookDTO>> getBooksInCategory(@PathVariable Long id) {
        List<Book> books = categoryService.getBooksInCategory(id);
        return ResponseEntity.ok(bookMapper.toDtoList(books));
    }


    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {

        categoryService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Category deleted successfully");

        return ResponseEntity.ok(response);

    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
}


    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable String id, @RequestBody CategoryDTO categoryDto) {

        Long idLong = Long.parseLong(id);
        Category updatedCategory = categoryService.update(idLong, categoryMapper.toEntity(categoryDto));
        return ResponseEntity.ok(categoryMapper.toDto(updatedCategory));
    }

}
