package com.scrable.bitirme.dto;

import com.scrable.bitirme.model.Order;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.List;

@Component
public class OrderDtoMapper {

        public OrderDto convertToDto(Order order) {
                List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                                .map(item -> new OrderItemDto(
                                                item.getProduct().getProductName(),
                                                item.getQuantity(),
                                                item.getPriceAtPurchase()))
                                .collect(Collectors.toList());

                return new OrderDto(
                                order.getId(),
                                order.getTotalAmount(),
                                order.getOrderDate(),
                                order.getStatus(),
                                order.getShippingAddress() != null
                                                ? order.getShippingAddress().getTitle() + " - "
                                                                + order.getShippingAddress().getDetails() + ", "
                                                                + order.getShippingAddress().getDistrict() + "/"
                                                                + order.getShippingAddress().getCity()
                                                : "Teslimat Adresi Yok",
                                itemDtos);
        }
}
