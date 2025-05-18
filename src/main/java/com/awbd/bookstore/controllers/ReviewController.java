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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

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
            logger.info("Created review for book ID: {}", reviewDTO.getBookId());
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toDto(savedReview));


        } catch (Exception e) {
            logger.error("Error creating review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByBookId(@PathVariable Long bookId) {
        try {
            List<Review> reviews = reviewService.getReviewsByBookId(bookId);
            logger.info("Fetched reviews for book ID: {}", bookId);
            return ResponseEntity.ok(reviewMapper.toDtoList(reviews));
        } catch (Exception e) {
            logger.error("Error fetching reviews for book ID {}: {}", bookId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}