package com.pharmacy.productservice.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.productservice.message.PromotionUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Subscriber lắng nghe channel promotion-updates.
 * Khi có message → evict cache của productId tương ứng.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionUpdateSubscriber {

    private static final String PRODUCTS_CACHE = "products";

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    public void onMessage(String jsonMessage) {
        try {
            PromotionUpdateMessage message = objectMapper.readValue(
                    jsonMessage, PromotionUpdateMessage.class);

            log.info("┌─ [SUBSCRIBER] Nhận promotion update");
            log.info("│  productId       : {}", message.getProductId());
            log.info("│  action          : {}", message.getAction());
            log.info("│  promotionName   : {}", message.getPromotionName());
            log.info("│  timestamp       : {}", message.getTimestamp());

            // ===== EVICT CACHE =====
            if ("EVICT_CACHE".equals(message.getAction())) {
                Cache cache = cacheManager.getCache(PRODUCTS_CACHE);
                if (cache != null) {
                    cache.evict(message.getProductId());
                    log.info("│  ✅ Đã xóa cache: {}::{}",
                            PRODUCTS_CACHE, message.getProductId());
                } else {
                    log.warn("│  ⚠️ Cache '{}' không tồn tại", PRODUCTS_CACHE);
                }
            }

            log.info("└─ [SUBSCRIBER] Xử lý xong");

        } catch (Exception e) {
            log.error("Lỗi xử lý promotion message: {}", e.getMessage(), e);
        }
    }
}