package com.pharmacy.promotionservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.promotionservice.message.PromotionUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionUpdatePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.redis.channel.promotion-updates}")
    private String channel;

    public void publishPromotionUpdate(Long productId, String promotionName) {
        PromotionUpdateMessage message = PromotionUpdateMessage.builder()
                .productId(productId)
                .action("EVICT_CACHE")
                .promotionName(promotionName)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            String json = objectMapper.writeValueAsString(message);

            Long receivers = redisTemplate.convertAndSend(channel, json);

            log.info(">>> PUBLISHED tới [{}]: {} (subscribers: {})",
                    channel, json, receivers);

        } catch (Exception e) {
            log.error(">>> Publish thất bại: {}", e.getMessage(), e);
        }
    }
}