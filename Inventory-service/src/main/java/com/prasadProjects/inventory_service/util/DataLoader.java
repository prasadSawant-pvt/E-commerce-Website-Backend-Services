package com.prasadProjects.inventory_service.util;

import com.prasadProjects.inventory_service.model.Inventory;
import com.prasadProjects.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final InventoryRepository inventoryRepository;
    @Override
    public void run(String... args) throws Exception {
        Inventory inventory = new Inventory();
        inventory.setSkuCode("iphone_13");
        inventory.setQuantity(100);

        Inventory inventory1 = new Inventory();
        inventory1.setSkuCode("iphone_13_red");
        inventory1.setQuantity(100);

        Inventory inventory3= new Inventory();
        inventory.setSkuCode("iphone_15");
        inventory.setQuantity(1000);

        inventoryRepository.save(inventory);
        inventoryRepository.save(inventory3);
        inventoryRepository.save(inventory1);
    }
}
