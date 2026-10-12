package com.pharmacy.promotionservice.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Message gửi qua Redis Pub/Sub.
 * Product-Service nhận và xóa cache của productId tương ứng.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PromotionUpdateMessage {
    private Long productId;
    private String action;        // EVICT_CACHE, REFRESH, ...
    private String promotionName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}