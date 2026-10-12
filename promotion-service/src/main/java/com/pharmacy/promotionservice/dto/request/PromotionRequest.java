package com.pharmacy.promotionservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequest {

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    private String name;

    @NotNull(message = "productId không được để trống")
    @Min(value = 1)
    private Long productId;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @DecimalMin(value = "0.0", message = "Phần trăm giảm giá không được âm")
    @DecimalMax(value = "100.0", message = "Phần trăm giảm giá không vượt quá 100")
    private BigDecimal discountPercent;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    private Boolean active = true;
}