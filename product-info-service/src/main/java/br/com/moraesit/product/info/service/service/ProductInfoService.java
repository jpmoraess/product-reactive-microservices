package br.com.moraesit.product.info.service.service;

import br.com.moraesit.product.info.service.domain.ProductInfo;
import br.com.moraesit.product.info.service.repository.ProductInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductInfoService {
    private final ProductInfoRepository productInfoRepository;

    public ProductInfoService(ProductInfoRepository productInfoRepository) {
        this.productInfoRepository = productInfoRepository;
    }

    public Mono<ProductInfo> addProductInfo(ProductInfo productInfo) {
        return productInfoRepository.save(productInfo);
    }

    public Flux<ProductInfo> getAllProductInfos() {
        return productInfoRepository.findAll();
    }

    public Mono<ProductInfo> getProductInfoById(String id) {
        return productInfoRepository.findById(id);
    }

    public Mono<ProductInfo> updateProductInfo(String id, ProductInfo updatedProductInfo) {
        return productInfoRepository.findById(id)
                .flatMap(productInfo -> {
                    productInfo.setName(updatedProductInfo.getName());
                    productInfo.setDescription(updatedProductInfo.getDescription());
                    productInfo.setPrice(updatedProductInfo.getPrice());
                    productInfo.setProperties(updatedProductInfo.getProperties());
                    return productInfoRepository.save(productInfo);
                });
    }

    public Mono<Void> deleteProductInfo(String id) {
        return productInfoRepository.deleteById(id);
    }
}
