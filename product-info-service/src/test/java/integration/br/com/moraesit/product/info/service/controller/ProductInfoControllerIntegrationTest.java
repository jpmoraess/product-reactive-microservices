package br.com.moraesit.product.info.service.controller;

import br.com.moraesit.product.info.service.domain.ProductInfo;
import br.com.moraesit.product.info.service.repository.ProductInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ProductInfoControllerIntegrationTest {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private WebTestClient webTestClient;

    private final String ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb41";

    static String PRODUCT_INFOS_URL = "/v1/product-infos";

    @BeforeEach
    void setUp() {
        var products = Arrays.asList(
                ProductInfo.builder()
                        .id(ID)
                        .name("Samsung Galaxy A13")
                        .description("Smartphone Samsung Galaxy A13 128GB Azul 4G")
                        .price(new BigDecimal("1200.50"))
                        .properties(Map.of("Marca", "Samsung", "Modelo", "A13", "Cor", "Azul"))
                        .build(),
                ProductInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Motorola Moto G52")
                        .description("Smartphone Motorola Moto G52 128GB Azul 4G")
                        .price(new BigDecimal("1310.16"))
                        .properties(Map.of("Marca", "Motorola", "Modelo", "G52", "Cor", "Preto"))
                        .build());

        productInfoRepository.saveAll(products).blockLast();
    }

    @AfterEach
    void tearDown() {
        productInfoRepository.deleteAll().block();
    }

    @Test
    void addProductInfo() {
        var productInfo = ProductInfo.builder()
                .id(null)
                .name("Motorola Moto G80")
                .description("Smartphone Motorola Moto G80 256GB Azul 4G")
                .price(new BigDecimal("2310.16"))
                .properties(Map.of("Marca", "Motorola", "Modelo", "G80", "Cor", "Preto"))
                .build();

        webTestClient
                .post()
                .uri(PRODUCT_INFOS_URL)
                .bodyValue(productInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProductInfo.class)
                .consumeWith(productInfoEntityExchangeResult -> {
                    ProductInfo savedProductInfo = productInfoEntityExchangeResult.getResponseBody();

                    assertNotNull(savedProductInfo);
                    assertNotNull(savedProductInfo.getId());
                });
    }

    @Test
    void updateProductInfo() {
        var productInfo = ProductInfo.builder()
                .id(ID)
                .name("Samsung Galaxy A13 Updated")
                .description("Smartphone Samsung Galaxy A13 128GB Azul 4G Updated")
                .price(new BigDecimal("1151.24"))
                .properties(Map.of("Marca", "Samsung", "Modelo", "A13"))
                .build();

        webTestClient
                .put()
                .uri(PRODUCT_INFOS_URL + "/{id}", ID)
                .bodyValue(productInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(ProductInfo.class)
                .consumeWith(productInfoEntityExchangeResult -> {
                    ProductInfo updatedProductInfo = productInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedProductInfo);
                    assertNotNull(updatedProductInfo.getId());
                    assertEquals("Samsung Galaxy A13 Updated", updatedProductInfo.getName());
                    assertEquals("Smartphone Samsung Galaxy A13 128GB Azul 4G Updated", updatedProductInfo.getDescription());
                    assertEquals(new BigDecimal("1151.24"), updatedProductInfo.getPrice());
                    assertEquals(2, updatedProductInfo.getProperties().size());
                });
    }

    @Test
    void getAllProductInfos() {
        webTestClient
                .get()
                .uri(PRODUCT_INFOS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(ProductInfo.class)
                .hasSize(2)
                .consumeWith(productInfoEntityListResult -> {
                    List<ProductInfo> productInfos = productInfoEntityListResult.getResponseBody();
                    assertNotNull(productInfos);
                    assertEquals(2, productInfos.size());
                });
    }

    @Test
    void getProductInfoById() {
        webTestClient
                .get()
                .uri(PRODUCT_INFOS_URL + "/{id}", ID)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo("Samsung Galaxy A13");
//                .expectBody(ProductInfo.class)
//                .consumeWith(productInfoEntityResult -> {
//                    ProductInfo productInfo = productInfoEntityResult.getResponseBody();
//                    assertNotNull(productInfo);
//                    assertEquals(ID, productInfo.getId());
//                    assertEquals("Samsung Galaxy A13", productInfo.getName());
//                    assertEquals("Smartphone Samsung Galaxy A13 128GB Azul 4G", productInfo.getDescription());
//                    assertEquals(new BigDecimal("1200.50"), productInfo.getPrice());
//                    assertNotNull(productInfo.getProperties());
//                    assertEquals(3, productInfo.getProperties().size());
//                });
    }

    @Test
    void deleteProductInfo() {
        webTestClient
                .delete()
                .uri(PRODUCT_INFOS_URL + "/{id}", ID)
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient
                .get()
                .uri(PRODUCT_INFOS_URL)
                .exchange()
                .expectBodyList(ProductInfo.class)
                .hasSize(1)
                .consumeWith(productInfoEntityListResult -> {
                    List<ProductInfo> productInfos = productInfoEntityListResult.getResponseBody();
                    assertNotNull(productInfos);
                    assertEquals(1, productInfos.size());
                    assertEquals("Motorola Moto G52", productInfos.get(0).getName());
                    assertEquals("Smartphone Motorola Moto G52 128GB Azul 4G", productInfos.get(0).getDescription());
                });
    }
}