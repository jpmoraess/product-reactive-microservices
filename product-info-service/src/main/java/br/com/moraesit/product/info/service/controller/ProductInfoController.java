package br.com.moraesit.product.info.service.controller;

import br.com.moraesit.product.info.service.domain.ProductInfo;
import br.com.moraesit.product.info.service.service.ProductInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/product-infos")
public class ProductInfoController {
    private final ProductInfoService productInfoService;

    public ProductInfoController(ProductInfoService productInfoService) {
        this.productInfoService = productInfoService;
    }

    @GetMapping
    public Flux<ProductInfo> getAllProductInfos() {
        return productInfoService.getAllProductInfos();
    }

    @GetMapping("/{id}")
    public Mono<ProductInfo> getProductInfoById(@PathVariable String id) {
        return productInfoService.getProductInfoById(id).log();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductInfo> addProductInfo(@RequestBody ProductInfo productInfo) {
        return productInfoService.addProductInfo(productInfo);
    }

    @PutMapping("/{id}")
    public Mono<ProductInfo> updateProductInfo(@PathVariable String id, @RequestBody ProductInfo updatedProductInfo) {
        return productInfoService.updateProductInfo(id, updatedProductInfo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProductInfo(@PathVariable String id) {
        return productInfoService.deleteProductInfo(id);
    }
}
