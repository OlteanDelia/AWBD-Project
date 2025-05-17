package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.ReviewDTO;
import com.awbd.bookstore.mappers.ReviewMapper;
import com.awbd.bookstore.models.Review;
import com.awbd.bookstore.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        try {
            Review review = reviewMapper.toEntity(reviewDTO);
            Review savedReview = reviewService.addReview(review, reviewDTO.getBookId(), reviewDTO.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toDto(savedReview));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByBookId(@PathVariable Long bookId) {
        try {
            List<Review> reviews = reviewService.getReviewsByBookId(bookId);
            return ResponseEntity.ok(reviewMapper.toDtoList(reviews));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}