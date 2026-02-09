package com.allobank.idr_rate_aggregator.controller;

import com.allobank.idr_rate_aggregator.model.ResourceType;
import com.allobank.idr_rate_aggregator.service.DataAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller utama untuk endpoint finance.
 *
 * Constraint:
 * - Hanya 1 endpoint
 * - Tidak boleh ada if/else atau switch untuk memilih resource
 * - Data harus diambil dari in-memory store
 */
@Slf4j
@RestController
@RequestMapping("/api/finance/data")
@RequiredArgsConstructor
public class FinanceDataController {

    private final DataAggregatorService dataAggregatorService;

    /**
     * Endpoint tunggal untuk semua resource type
     *
     * Contoh:
     * /api/finance/data/latest_idr_rates
     * /api/finance/data/historical_idr_usd
     * /api/finance/data/supported_currencies
     */
    @GetMapping("/{resourceType}")
    public ResponseEntity<?> getFinanceData(@PathVariable String resourceType) {

        log.info("Request diterima untuk resourceType: {}", resourceType);

        try {

            // Konversi string ke enum
            ResourceType type = ResourceType.fromString(resourceType);

            // Ambil data dari in-memory store
            Object data = dataAggregatorService.getData(type);

            return ResponseEntity.ok(data);

        } catch (IllegalArgumentException e) {

            log.error("Resource type tidak valid: {}", resourceType);

            return ResponseEntity
                    .badRequest()
                    .body("Resource type tidak valid: " + resourceType);

        } catch (Exception e) {

            log.error("Terjadi kesalahan saat mengambil data", e);

            return ResponseEntity
                    .internalServerError()
                    .body("Terjadi kesalahan internal");
        }
    }
}