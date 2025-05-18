package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.SaleDTO;
import com.awbd.bookstore.models.Sale;
import com.awbd.bookstore.services.SaleService;
import com.awbd.bookstore.mappers.SaleMapper;
import com.awbd.bookstore.annotations.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;
    private final SaleMapper saleMapper;
    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);

    public SaleController(SaleService saleService, SaleMapper saleMapper) {
        this.saleService = saleService;
        this.saleMapper = saleMapper;
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<SaleDTO> createSale(@RequestBody SaleDTO saleDTO) {
        Sale sale = saleMapper.toEntity(saleDTO);
        Sale saved = saleService.create(sale, saleDTO.getCategoryIds());
        logger.info("Created sale with ID: {}", saved.getId());
        return ResponseEntity.ok(saleMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        Sale sale = saleService.getById(id);
        if (sale == null) {
            logger.warn("Sale with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Retrieved sale with ID: {}", id);
        return ResponseEntity.ok(saleMapper.toDto(sale));
    }

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        List<Sale> sales = saleService.getAll();
        List<SaleDTO> dtos = sales.stream()
                .map(saleMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Retrieved {} sales", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<SaleDTO> updateSale(@PathVariable Long id, @RequestBody SaleDTO saleDTO) {
        Sale sale = saleMapper.toEntity(saleDTO);
        Sale updated = saleService.update(id, sale, saleDTO.getCategoryIds());
        if (updated == null) {
            logger.warn("Sale with ID {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Updated sale with ID: {}", id);
        return ResponseEntity.ok(saleMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.delete(id);
        logger.info("Deleted sale with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
