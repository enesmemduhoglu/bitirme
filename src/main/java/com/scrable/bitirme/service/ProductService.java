package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.ProductDto;
import com.scrable.bitirme.dto.ProductDtoMapper;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductDtoMapper productDtoMapper;

    public ProductDto createProduct(ProductDto productDto) {
        Product product = productDtoMapper.toEntity(productDto);
        Product savedProduct = productRepo.save(product);
        return productDtoMapper.toDto(savedProduct);
    }

    public List<ProductDto> getProducts() {
        return productRepo.findAll()
                .stream()
                .map(productDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Optional<Product> existingProductOptional = productRepo.findById(id);

        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();
            productDtoMapper.updateProductFromDto(productDto, existingProduct);
            Product updatedProduct = productRepo.save(existingProduct);

            return productDtoMapper.toDto(updatedProduct);
        } else {
            throw new RuntimeException("Product not found with id: " + id);

        }
    }

    public void deleteProduct(Long id) {
        if (!productRepo.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepo.deleteById(id);
    }

}
