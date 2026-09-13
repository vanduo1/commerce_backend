package com.sara.commerce_platform.product;

import com.sara.commerce_platform.product.dto.ProductCreateRequest;
import com.sara.commerce_platform.product.dto.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct (ProductCreateRequest request);
    ProductResponse toProductResponse (Product product);
}
