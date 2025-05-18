package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.AuthorDTO;
import com.awbd.bookstore.models.Author;
import com.awbd.bookstore.services.AuthorService;
import com.awbd.bookstore.mappers.AuthorMapper;
import com.awbd.bookstore.annotations.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorDTO authorDTO) {
        Author author = authorMapper.toEntity(authorDTO);
        Author savedAuthor = authorService.create(author);
        return ResponseEntity.ok(authorMapper.toDto(savedAuthor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getById(id);
        return ResponseEntity.ok(authorMapper.toDto(author));
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<Author> authors = authorService.getAll();
        List<AuthorDTO> authorDTOs = authors.stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(authorDTOs);
    }

    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody AuthorDTO authorDTO) {
        Author updatedAuthor = authorMapper.toEntity(authorDTO);
        Author savedAuthor = authorService.update(id, updatedAuthor);
        return ResponseEntity.ok(authorMapper.toDto(savedAuthor));
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
