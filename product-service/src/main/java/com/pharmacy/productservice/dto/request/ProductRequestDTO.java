package com.pharmacy.productservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Danh mục không được để trống")
    private String category;

    @Size(max = 5000)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 1, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
}