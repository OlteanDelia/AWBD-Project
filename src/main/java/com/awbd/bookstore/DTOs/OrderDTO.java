package com.awbd.bookstore.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private Set<Long> bookIds;
    private LocalDateTime orderDate;


    public OrderDTO(Long userId, Set<Long> bookIds) {
        this.userId = userId;
        this.bookIds = bookIds;
        this.orderDate = LocalDateTime.now();
    }

    public OrderDTO(Long userId, Set<Long> bookIds, LocalDateTime orderDate) {
        this.userId = userId;
        this.bookIds = bookIds;
        this.orderDate = orderDate;
    }
}
