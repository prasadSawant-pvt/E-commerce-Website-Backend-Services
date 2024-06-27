package com.prasadProjects.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prasadProjects.productservice.dto.ProductRequest;
import com.prasadProjects.productservice.model.ProductDetails;
import com.prasadProjects.productservice.model.ProductType;
import com.prasadProjects.productservice.repository.ProductRepository;
import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;

	static {
		mongoDBContainer.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
		dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString))
				.andExpect(status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{category}","BOOKS")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		Assertions.assertEquals(getProductRequest().getName(), productRepository.findAll().get(0).getName());
		Assertions.assertEquals(1, productRepository.findAll().size());
	}


	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.productDetails(ProductDetails.builder()
						.productType(ProductType.BOOKS)
						.brand("Oracle")
						.yearOfManufacture("2010")
						.build())
				.price(BigDecimal.valueOf(1200))
				.build();
	}

}
