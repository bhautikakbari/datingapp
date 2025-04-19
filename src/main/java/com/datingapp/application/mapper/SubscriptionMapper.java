package com.datingapp.application.mapper;

import com.datingapp.application.dto.response.SubscriptionPlanResponseDto;
import com.datingapp.application.dto.response.SubscriptionResponseDto;
import com.datingapp.domain.entity.SubscriptionPlan;
import com.datingapp.domain.entity.UserSubscription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {
    
    private final ObjectMapper objectMapper;
    
    public SubscriptionResponseDto toResponseDto(UserSubscription subscription) {
        if (subscription == null) {
            return null;
        }
        
        Map<String, Object> features;
        try {
            features = objectMapper.readValue(subscription.getPlan().getFeatures(), Map.class);
        } catch (JsonProcessingException e) {
            features = Map.of();
        }
        
        boolean isActive = subscription.getEndDate().isAfter(LocalDateTime.now());
        
        return SubscriptionResponseDto.builder()
                .id(subscription.getId())
                .planName(subscription.getPlan().getName())
                .endDate(subscription.getEndDate())
                .features(features)
                .isActive(isActive)
                .build();
    }
    
    public SubscriptionPlanResponseDto toPlanResponseDto(SubscriptionPlan plan) {
        Map<String, Object> features;
        try {
            features = objectMapper.readValue(plan.getFeatures(), Map.class);
        } catch (JsonProcessingException e) {
            features = Map.of();
        }
        
        return SubscriptionPlanResponseDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .features(features)
                .build();
    }
}
