package com.prasadProjects.inventory_service.controller;

import com.prasadProjects.inventory_service.dto.InventoryResponse;
import com.prasadProjects.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{skuCode}")
    public ResponseEntity<Boolean> isInStock(@PathVariable String skuCode) {
        log.info("Received inventory check request for skuCode: {}", skuCode);
        try {
            boolean inStock = inventoryService.isItemIsInStock(skuCode);
            return new ResponseEntity<>(inStock, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error checking inventory for skuCode: {}", skuCode, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> isInStock(@RequestParam List<String> skuCodes) {
        log.info("Received inventory check request for skuCodes: {}", skuCodes);
        try {
            List<InventoryResponse> inventoryResponses = inventoryService.isInStock(skuCodes);
            return new ResponseEntity<>(inventoryResponses, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error checking inventory for skuCodes: {}", skuCodes, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
