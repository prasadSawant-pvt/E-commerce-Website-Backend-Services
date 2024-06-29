package com.prasadProjects.inventory_service.controller;

import com.prasadProjects.inventory_service.dto.InventoryResponse;
import com.prasadProjects.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable String skuCode) {
        log.info("Received inventory check request for skuCode: {}", skuCode);
        return inventoryService.isItemIsInStock(skuCode);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCodes) {
        log.info("Received inventory check request for skuCode: {}", skuCodes);
       try{
        return inventoryService.isInStock(skuCodes);
       }catch (Exception e){
           return inventoryService.isInStock(skuCodes);
       }
    }
}

