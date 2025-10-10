package com.scrable.bitirme.dto;

import com.scrable.bitirme.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {
    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto productDto = new ProductDto();
        productDto.setProductId(product.getProductId());
        productDto.setProductName(product.getProductName());
        productDto.setProductDescription(product.getProductDescription());
        productDto.setProductPrice(product.getProductPrice());
        productDto.setProductStock(product.getProductStock());
        productDto.setProductImage(product.getProductImage());

        return productDto;
    }

    public Product toEntity(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        Product product = new Product();
        product.setProductId(productDto.getProductId());
        product.setProductName(productDto.getProductName());
        product.setProductDescription(productDto.getProductDescription());
        product.setProductPrice(productDto.getProductPrice());
        product.setProductStock(productDto.getProductStock());
        product.setProductImage(productDto.getProductImage());

        return product;
    }

    public void updateProductFromDto(ProductDto productDto, Product product) {
        if (productDto == null || product == null) {
            return;
        }

        if (productDto.getProductName() != null) {
            product.setProductName(productDto.getProductName());
        }
        if (productDto.getProductDescription() != null) {
            product.setProductDescription(productDto.getProductDescription());
        }
        if (productDto.getProductPrice() != null) {
            product.setProductPrice(productDto.getProductPrice());
        }
        if (productDto.getProductStock() != null) {
            product.setProductStock(productDto.getProductStock());
        }
        if (productDto.getProductImage() != null) {
            product.setProductImage(productDto.getProductImage());
        }
    }
}

