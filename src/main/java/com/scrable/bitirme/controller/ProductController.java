package com.scrable.bitirme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scrable.bitirme.dto.ProductDto;
import com.scrable.bitirme.service.ProductService;
import com.scrable.bitirme.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final SearchService searchService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductDto> createProduct(@RequestPart("product") String productJson,
                                                    @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        ProductDto productDto = objectMapper.readValue(productJson, ProductDto.class);

        ProductDto createdProduct = productService.createProduct(productDto, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("q") String q) {
        List<ProductDto> products = searchService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                    @RequestPart("product") String productJson,
                                                    @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        ProductDto productDto = objectMapper.readValue(productJson, ProductDto.class);

        ProductDto updatedProduct = productService.updateProduct(id, productDto, imageFile);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
