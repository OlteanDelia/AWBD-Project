package com.awbd.bookstore.controllers;

import com.awbd.bookstore.DTOs.SaleDTO;
import com.awbd.bookstore.models.Sale;
import com.awbd.bookstore.services.SaleService;
import com.awbd.bookstore.mappers.SaleMapper;
import com.awbd.bookstore.annotations.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private SaleService saleService;
    private SaleMapper saleMapper;
    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);

    public SaleController(SaleService saleService, SaleMapper saleMapper) {
        this.saleService = saleService;
        this.saleMapper = saleMapper;
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<Sale> createSale(
            @RequestBody
            @Valid
            SaleDTO saleDTO) {
        Sale sale = saleMapper.toEntity(saleDTO);
        Sale saved = saleService.create(sale, saleDTO.getCategoryIds());
        logger.info("Created sale with ID: {}", saved.getId());

        return ResponseEntity.created(URI.create("/api/sales/" + saved.getId()))
                .body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        Sale sale = saleService.getById(id);
        logger.info("Retrieved sale with ID: {}", id);
        return ResponseEntity.ok(sale);
    }

    @GetMapping
    public List<Sale> getAllSales() {
        List<Sale> sales = saleService.getAll();
        logger.info("Retrieved {} sales", sales.size());
        return sales;
    }

    @GetMapping("/active")
    public List<Sale> getActiveSales() {
        List<Sale> sales = saleService.getAllActiveSales();
        logger.info("Retrieved {} active sales", sales.size());
        return sales;
    }

    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Sale> updateSale(
            @PathVariable
            Long id,

            @RequestBody
            @Valid
            SaleDTO saleDTO) {

        if (saleDTO.getId() != null && !id.equals(saleDTO.getId())) {
            logger.warn("ID mismatch: path ID {} doesn't match body ID {}", id, saleDTO.getId());
            throw new RuntimeException("Id from path does not match with id from request");
        }

        Sale sale = saleMapper.toEntity(saleDTO);
        Sale updated = saleService.update(id, sale, saleDTO.getCategoryIds());
        logger.info("Updated sale with ID: {}", id);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.delete(id);
        logger.info("Deleted sale with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}