package com.scrable.bitirme.dto;

import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.model.document.ProductDocument;
import org.springframework.stereotype.Component;

@Component
public class ProductDocumentMapper {

    public ProductDocument convertToDocument(Product product) {
        ProductDocument document = new ProductDocument();
        document.setId(product.getProductId());
        document.setProductName(product.getProductName());
        document.setProductDescription(product.getProductDescription());
        document.setProductPrice(product.getProductPrice());
        return document;
    }
}
