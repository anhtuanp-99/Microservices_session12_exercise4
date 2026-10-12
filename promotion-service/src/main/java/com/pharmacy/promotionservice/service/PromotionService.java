package com.pharmacy.promotionservice.service;

import com.pharmacy.promotionservice.dto.request.PromotionRequest;
import com.pharmacy.promotionservice.dto.response.PromotionResponse;
import com.pharmacy.promotionservice.entity.Promotion;
import com.pharmacy.promotionservice.exception.ResourceNotFoundException;
import com.pharmacy.promotionservice.publisher.PromotionUpdatePublisher;
import com.pharmacy.promotionservice.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionUpdatePublisher publisher;

    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        Promotion promotion = Promotion.builder()
                .name(request.getName())
                .productId(request.getProductId())
                .discountPercent(request.getDiscountPercent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        Promotion saved = promotionRepository.save(promotion);
        log.info("Đã tạo promotion id={}, productId={}", saved.getId(), saved.getProductId());

        // Publish event để Product-Service xóa cache
        publisher.publishPromotionUpdate(saved.getProductId(), saved.getName());

        return toResponse(saved);
    }

    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "ID", id));

        promotion.setName(request.getName());
        promotion.setProductId(request.getProductId());
        promotion.setDiscountPercent(request.getDiscountPercent());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.getActive());

        Promotion updated = promotionRepository.save(promotion);
        log.info("Đã cập nhật promotion id={}, productId={}", id, updated.getProductId());

        // Publish event
        publisher.publishPromotionUpdate(updated.getProductId(), updated.getName());

        return toResponse(updated);
    }

    private PromotionResponse toResponse(Promotion p) {
        return PromotionResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .productId(p.getProductId())
                .discountPercent(p.getDiscountPercent())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .active(p.getActive())
                .build();
    }
}