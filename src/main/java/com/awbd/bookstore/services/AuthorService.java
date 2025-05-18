package com.awbd.bookstore.services;

import com.awbd.bookstore.exceptions.AuthorNotFoundException;
import com.awbd.bookstore.models.Author;
import com.awbd.bookstore.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author create(Author author) {
        if (authorRepository.existsByNameAndBirthDate(author.getName(), author.getBirthDate())) {
            throw new IllegalStateException(
                    "Author with name '" + author.getName() + "' and birth date '" + author.getBirthDate() + "' already exists."
            );
        }
        return authorRepository.save(author);
    }

    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
    }

    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    public Author update(Long id, Author updatedAuthor) {
        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    existingAuthor.setName(updatedAuthor.getName());
                    existingAuthor.setBiography(updatedAuthor.getBiography());
                    existingAuthor.setBirthDate(updatedAuthor.getBirthDate());
                    return authorRepository.save(existingAuthor);
                })
                .orElseThrow(() -> new AuthorNotFoundException(id));
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new AuthorNotFoundException(id);
        }
        authorRepository.deleteById(id);
    }
}

