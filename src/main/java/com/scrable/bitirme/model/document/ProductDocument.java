package com.scrable.bitirme.model.document;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Document(indexName = "products")
@Getter
@Setter
public class ProductDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "productName")
    private String productName;

    @Field(type = FieldType.Text, name = "productDescription")
    private String productDescription;

    @Field(type = FieldType.Double, name = "productPrice")
    private BigDecimal productPrice;
}
