package br.com.moraesit.product.info.service.service;

import br.com.moraesit.product.info.service.domain.ProductInfo;
import br.com.moraesit.product.info.service.repository.ProductInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProductInfoServiceUnitTest {

    @InjectMocks
    private ProductInfoService productInfoService;

    @Mock
    private ProductInfoRepository productInfoRepository;

    @Test
    void getProductInfoById() {
        String id = UUID.randomUUID().toString();

        when(productInfoRepository.findById(isA(String.class))).thenReturn(Mono.just(
                ProductInfo.builder()
                        .id(id)
                        .name("Product Name")
                        .description("Product Description")
                        .price(new BigDecimal("200.00"))
                        .properties(Map.of("key", "value"))
                        .build()
        ));

        Mono<ProductInfo> productInfoMono = productInfoService.getProductInfoById(id);

        StepVerifier.create(productInfoMono)
                .consumeNextWith(productInfo -> {
                    assertNotNull(productInfo.getId());
                    assertEquals("Product Name", productInfo.getName());
                    assertEquals("Product Description", productInfo.getDescription());
                    assertEquals(new BigDecimal("200.00"), productInfo.getPrice());
                    assertNotNull(productInfo.getProperties());
                    assertEquals(1, productInfo.getProperties().size());
                }).verifyComplete();
    }

    @Test
    void getAllProductInfos() {
        when(productInfoRepository.findAll()).thenReturn(Flux.fromIterable(
                Arrays.asList(ProductInfo.builder()
                                .id("123")
                                .name("Product Name 1")
                                .description("Product Description 1")
                                .price(new BigDecimal("200.00"))
                                .properties(Map.of("key", "value"))
                                .build(),
                        ProductInfo.builder()
                                .id("456")
                                .name("Product Name 2")
                                .description("Product Description 2")
                                .price(new BigDecimal("300.00"))
                                .properties(Map.of("key", "value"))
                                .build())
        ));

        Flux<ProductInfo> productInfoFlux = productInfoService.getAllProductInfos();

        StepVerifier.create(productInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }
}
