package com.sara.commerce_platform.product;

import com.sara.commerce_platform.common.exception.ProductNotFoundException;
import com.sara.commerce_platform.product.dto.ProductCreateRequest;
import com.sara.commerce_platform.product.dto.ProductResponse;

import com.sara.commerce_platform.product.dto.ProductUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public ProductService(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProduct(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(productMapper::toProductResponse);
    }

    @Cacheable(value = "product", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {
        log.info("Calling database");

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(id));
        return productMapper.toProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product =  productMapper.toProduct(request);
        product.setStock(100);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct( String id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        if(request.price() != null) {
            product.setPrice(request.price());
        }
        if(request.stock() != null) {
            product.setStock(request.stock());
        }

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }







}
