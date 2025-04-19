package com.datingapp.application.service;

import com.datingapp.application.dto.response.SubscriptionPlanResponseDto;
import com.datingapp.application.dto.response.SubscriptionResponseDto;
import com.datingapp.application.exception.ResourceNotFoundException;
import com.datingapp.application.mapper.SubscriptionMapper;
import com.datingapp.domain.entity.SubscriptionPlan;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.entity.UserSubscription;
import com.datingapp.domain.repository.SubscriptionPlanRepository;
import com.datingapp.domain.repository.UserSubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    @Autowired
    public SubscriptionPlanRepository subscriptionPlanRepository;
    @Autowired
    public UserSubscriptionRepository userSubscriptionRepository;
    @Autowired
    public AuthService authService;
    @Autowired
    public SubscriptionMapper subscriptionMapper;
    @Autowired
    public ObjectMapper objectMapper;
    
    public List<SubscriptionPlanResponseDto> getAllPlans() {
        return subscriptionPlanRepository.findAll().stream()
                .map(subscriptionMapper::toPlanResponseDto)
                .collect(Collectors.toList());
    }
    
    public SubscriptionResponseDto getCurrentSubscription() {
        User user = authService.getCurrentUser();
        Optional<UserSubscription> subscription = userSubscriptionRepository.findActiveSubscriptionByUserId(
                user.getId(), LocalDateTime.now());
        
        return subscription.map(subscriptionMapper::toResponseDto).orElse(null);
    }
    
    @Transactional
    public SubscriptionResponseDto subscribe(Long planId) {
        User user = authService.getCurrentUser();
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
        
        // Cancel any active subscriptions
        Optional<UserSubscription> activeSubscription = userSubscriptionRepository.findActiveSubscriptionByUserId(
                user.getId(), LocalDateTime.now());
        activeSubscription.ifPresent(sub -> {
            sub.setEndDate(LocalDateTime.now());
            userSubscriptionRepository.save(sub);
        });
        
        // Create new subscription
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = plan.getDurationDays() > 0 
                ? startDate.plusDays(plan.getDurationDays())
                : startDate.plusYears(100); // For "free" plans
        
        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .paymentStatus(UserSubscription.PaymentStatus.COMPLETED)
                .build();
        
        subscription = userSubscriptionRepository.save(subscription);
        
        return subscriptionMapper.toResponseDto(subscription);
    }
    
    public Map<String, Object> getFeatures(Long userId) {
        Optional<UserSubscription> subscription = userSubscriptionRepository.findActiveSubscriptionByUserId(
                userId, LocalDateTime.now());
        
        if (subscription.isEmpty()) {
            // Return free plan features
            SubscriptionPlan freePlan = subscriptionPlanRepository.findAll().stream()
                    .filter(plan -> plan.getPrice().doubleValue() == 0.0)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Free plan not found"));
            
            return parseFeatures(freePlan.getFeatures());
        }
        
        return parseFeatures(subscription.get().getPlan().getFeatures());
    }
    
    private Map<String, Object> parseFeatures(String featuresJson) {
        try {
            return objectMapper.readValue(featuresJson, Map.class);
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
