package com.pharmacy.productservice.mapper;

import com.pharmacy.productservice.dto.response.ProductResponseDTO;
import com.pharmacy.productservice.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) return null;
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<ProductResponseDTO> toResponseDTOList(List<Product> products) {
        if (products == null) return List.of();
        return products.stream().map(this::toResponseDTO).toList();
    }
}