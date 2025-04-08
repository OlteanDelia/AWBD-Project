package com.awbd.bookstore.mappers;

import com.awbd.bookstore.DTOs.OrderDTO;
import com.awbd.bookstore.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    private final BookMapper bookMapper;

    @Autowired
    public OrderMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setUserId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setBookIds(order.getBooks().stream()
                .map(book -> book.getId())
                .collect(Collectors.toSet()));
        dto.setOrderDate(order.getOrderDate());
        return dto;
    }


    public List<OrderDTO> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
