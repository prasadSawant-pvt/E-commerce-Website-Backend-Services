package com.prasadProjects.inventory_service.service;

import com.prasadProjects.inventory_service.dto.InventoryResponse;
import com.prasadProjects.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        log.info("Checking inventory for SKU codes: {}", skuCodes);
        try {
            return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                    .map(inventory ->
                            InventoryResponse.builder()
                                    .skuCode(inventory.getSkuCode())
                                    .isInStock(inventory.getQuantity() > 0)
                                    .build()
                    ).toList();
        } catch (Exception e) {
            log.error("Error occurred while checking inventory for SKU codes: {}", skuCodes, e);
            throw e;
        }
    }

    public boolean isItemIsInStock(String skuCode) {
        log.info("Checking inventory for SKU code: {}", skuCode);
        try {
            boolean isInStock = inventoryRepository.findBySkuCode(skuCode).isPresent();
            log.info("Inventory check for SKU code {}: {}", skuCode, isInStock ? "In stock" : "Out of stock");
            return isInStock;
        } catch (Exception e) {
            log.error("Error occurred while checking inventory for SKU code: {}", skuCode, e);
            throw e;
        }
    }
}
