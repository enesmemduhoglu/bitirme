package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.ProductDto;
import com.scrable.bitirme.dto.ProductDtoMapper;
import com.scrable.bitirme.exception.ProductNotFoundException;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
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

    public ProductDto createProduct(ProductDto productDto, MultipartFile imageFile) {
        Product product = productDtoMapper.toEntity(productDto);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageKey = fileStorageService.uploadFile(imageFile);
            product.setProductImage(imageKey);
        }

        Product savedProduct = productRepo.save(product);

        // dto'ya dönüştürürken S3 URL'ini de oluşturur
        ProductDto resultDto = productDtoMapper.toDto(savedProduct);
        resultDto.setProductImage(fileStorageService.generatePresignedUrl(savedProduct.getProductImage()));

        return resultDto;
    }

    public List<ProductDto> getProducts() {
        return productRepo.findAll()
                .stream()
                .map(product -> {
                    ProductDto dto = productDtoMapper.toDto(product);
                    // Her ürün için s3'ten geçici url alır ve dto'ya atar
                    dto.setProductImage(fileStorageService.generatePresignedUrl(product.getProductImage()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
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

        ProductDto resultDto = productDtoMapper.toDto(updatedProduct);
        resultDto.setProductImage(fileStorageService.generatePresignedUrl(updatedProduct.getProductImage()));

        return resultDto;
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product productToDelete = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        String imageKey = productToDelete.getProductImage();

        productRepo.delete(productToDelete);

        if (imageKey != null) {
            fileStorageService.deleteFile(imageKey);
        }
    }

}
