package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.ProductDto;
import com.scrable.bitirme.dto.ProductDtoMapper;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.model.document.ProductDocument;
import com.scrable.bitirme.repository.ProductRepo;
import com.scrable.bitirme.repository.ProductSearchRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductSearchRepo productSearchRepo;
    private final ProductRepo productRepo;
    private final FileStorageService fileStorageService;
    private final ProductDtoMapper productDtoMapper;

    public List<ProductDto> searchProducts(String searchTerm) {
        List<ProductDocument> documents = productSearchRepo.findByProductNameOrProductDescription(searchTerm, searchTerm);

        List<Long> productIds = documents.stream()
                .map(ProductDocument::getId)
                .collect(Collectors.toList());

        List<Product> products = productRepo.findAllById(productIds);

        return products.stream()
                .map(product -> {
                    ProductDto dto = productDtoMapper.toDto(product);
                    dto.setProductImage(fileStorageService.generatePresignedUrl(product.getProductImage()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
