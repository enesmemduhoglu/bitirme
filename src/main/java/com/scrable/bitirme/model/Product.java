package com.scrable.bitirme.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private Integer MaxQuantityPerCart;
    private BigDecimal productPrice;
    private Integer productStock;
}
