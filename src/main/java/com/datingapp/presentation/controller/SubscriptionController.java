package com.datingapp.presentation.controller;

import com.datingapp.application.dto.response.SubscriptionPlanResponseDto;
import com.datingapp.application.dto.response.SubscriptionResponseDto;
import com.datingapp.application.service.SubscriptionService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    
    @Autowired
    public SubscriptionService subscriptionService;
    
    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanResponseDto>> getAllPlans() {
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }
    
    @GetMapping("/current")
    public ResponseEntity<SubscriptionResponseDto> getCurrentSubscription() {
        SubscriptionResponseDto subscription = subscriptionService.getCurrentSubscription();
        return subscription != null 
                ? ResponseEntity.ok(subscription) 
                : ResponseEntity.noContent().build();
    }
    
    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<SubscriptionResponseDto> subscribe(@PathVariable Long planId) {
        return ResponseEntity.ok(subscriptionService.subscribe(planId));
    }
}
