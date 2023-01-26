package br.com.moraesit.product.info.service.repository;

import br.com.moraesit.product.info.service.domain.ProductInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class ProductInfoRepositoryIntegrationTest {
    @Autowired
    ProductInfoRepository productInfoRepository;

    private final String ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb41";

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
    void findAll() {
        Flux<ProductInfo> productInfoFlux = productInfoRepository.findAll().log();

        StepVerifier.create(productInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<ProductInfo> productInfoMono = productInfoRepository.findById(ID).log();

        StepVerifier.create(productInfoMono)
                .assertNext(productInfo -> {
                    assertNotNull(productInfo.getId());
                    assertEquals("Samsung Galaxy A13", productInfo.getName());
                    assertEquals("Smartphone Samsung Galaxy A13 128GB Azul 4G", productInfo.getDescription());
                })
                .verifyComplete();
    }

    @Test
    void save() {
        var product = ProductInfo.builder()
                .id(null)
                .name("Motorola Moto G80")
                .description("Smartphone Motorola Moto G80 256GB Azul 4G")
                .price(new BigDecimal("2310.16"))
                .properties(Map.of("Marca", "Motorola", "Modelo", "G80", "Cor", "Preto"))
                .build();

        var productInfoMono = productInfoRepository.save(product).log();

        StepVerifier.create(productInfoMono)
                .assertNext(productInfo -> {
                    assertNotNull(productInfo.getId());
                    assertEquals("Motorola Moto G80", productInfo.getName());
                    assertEquals(3, productInfo.getProperties().size());
                    assertEquals("Motorola", productInfo.getProperties().get("Marca"));
                })
                .verifyComplete();
    }

    @Test
    void update() {
        var product = productInfoRepository.findById(ID).block();

        assertNotNull(product);

        product.setPrice(new BigDecimal("1000.00"));

        var productInfoMono = productInfoRepository.save(product).log();

        StepVerifier.create(productInfoMono)
                .assertNext(productInfo -> {
                    assertNotNull(productInfo.getId());
                    assertEquals(new BigDecimal("1000.00"), productInfo.getPrice());
                })
                .verifyComplete();
    }

    @Test
    void delete() {
        productInfoRepository.deleteById(ID).block();

        var products = productInfoRepository.findAll().log();

        StepVerifier.create(products)
                .expectNextCount(1)
                .verifyComplete();
    }
}