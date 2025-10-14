package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepo extends ElasticsearchRepository<ProductDocument, Long> {
    List<ProductDocument> findByProductNameOrProductDescription(String name, String description);
}
