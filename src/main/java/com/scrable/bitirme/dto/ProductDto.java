package com.scrable.bitirme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer maxQuantityPerCart;
    private String productImage;
    private Integer productStock;
}
