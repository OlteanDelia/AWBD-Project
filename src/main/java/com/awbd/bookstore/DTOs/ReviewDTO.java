package com.awbd.bookstore.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String content;
    private int rating;
    private String createdAt;
    private Long userId;
    private Long bookId;

    public ReviewDTO(String content, int rating, String createdAt, Long userId, Long bookId) {
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
        this.userId = userId;
        this.bookId = bookId;
    }
}