package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.ProductDocumentMapper;
import com.scrable.bitirme.dto.ProductDto;
import com.scrable.bitirme.dto.ProductDtoMapper;
import com.scrable.bitirme.exception.ProductNotFoundException;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.repository.ProductRepo;
import com.scrable.bitirme.repository.ProductSearchRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductDtoMapper productDtoMapper;
    private final FileStorageService fileStorageService;
    private final ProductSearchRepo productSearchRepo;
    private final ProductDocumentMapper productDocumentMapper;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto createProduct(ProductDto productDto, MultipartFile imageFile) {
        Product product = productDtoMapper.toEntity(productDto);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageKey = fileStorageService.uploadFile(imageFile);
            product.setProductImage(imageKey);
        }

        Product savedProduct = productRepo.save(product);

        productSearchRepo.save(productDocumentMapper.convertToDocument(savedProduct));

        ProductDto resultDto = productDtoMapper.toDto(savedProduct);
        resultDto.setProductImage(fileStorageService.generatePresignedUrl(savedProduct.getProductImage()));

        return resultDto;
    }

    @Cacheable("products")
    public List<ProductDto> getProducts() {
        return productRepo.findAll()
                .stream()
                .map(product -> {
                    ProductDto dto = productDtoMapper.toDto(product);
                    dto.setProductImage(fileStorageService.generatePresignedUrl(product.getProductImage()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto updateProduct(Long id, ProductDto productDto, MultipartFile imageFile) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        String oldImageKey = existingProduct.getProductImage();

        productDtoMapper.updateProductFromDto(productDto, existingProduct);

        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageKey = fileStorageService.uploadFile(imageFile);
            existingProduct.setProductImage(newImageKey);

            if (oldImageKey != null) {
                fileStorageService.deleteFile(oldImageKey);
            }
        }

        Product updatedProduct = productRepo.save(existingProduct);

        productSearchRepo.save(productDocumentMapper.convertToDocument(updatedProduct));

        ProductDto resultDto = productDtoMapper.toDto(updatedProduct);
        resultDto.setProductImage(fileStorageService.generatePresignedUrl(updatedProduct.getProductImage()));

        return resultDto;
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        ProductDto dto = productDtoMapper.toDto(product);
        return dto;
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        Product productToDelete = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        String imageKey = productToDelete.getProductImage();

        productRepo.delete(productToDelete);

        productSearchRepo.deleteById(id);

        if (imageKey != null) {
            fileStorageService.deleteFile(imageKey);
        }
    }

}
