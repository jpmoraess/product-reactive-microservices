package br.com.moraesit.product.info.service.repository;

import br.com.moraesit.product.info.service.domain.ProductInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInfoRepository extends ReactiveMongoRepository<ProductInfo, String> {
}
