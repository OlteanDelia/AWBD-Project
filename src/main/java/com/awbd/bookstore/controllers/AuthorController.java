package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.AuthorDTO;
import com.awbd.bookstore.models.Author;
import com.awbd.bookstore.services.AuthorService;
import com.awbd.bookstore.mappers.AuthorMapper;
import com.awbd.bookstore.annotations.RequireAdmin;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private AuthorService authorService;
    private AuthorMapper authorMapper;

    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<Author> createAuthor(
            @RequestBody
            @Valid
            AuthorDTO authorDTO) {
        Author author = authorMapper.toEntity(authorDTO);
        Author createdAuthor = authorService.create(author);
        return ResponseEntity.created(URI.create("/api/authors/" + createdAuthor.getId()))
                .body(createdAuthor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getById(id);
        return ResponseEntity.ok(author);
    }

    @GetMapping
    public List<Author> getAllAuthors() {
        return authorService.getAll();
    }

    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Author> updateAuthor(
            @PathVariable
            Long id,

            @RequestBody
            @Valid
            AuthorDTO authorDTO) {
        if (authorDTO.getId() != null && !id.equals(authorDTO.getId())) {
            throw new RuntimeException("Id from path does not match with id from request");
        }

        Author author = authorMapper.toEntity(authorDTO);
        return ResponseEntity.ok()
                .body(authorService.update(id, author));
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}