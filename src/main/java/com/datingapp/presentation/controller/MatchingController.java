package com.datingapp.presentation.controller;

import com.datingapp.application.dto.request.SwipeRequestDto;
import com.datingapp.application.dto.response.SwipeResponseDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.application.service.MatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {
    
    @Autowired
    public MatchingService matchingService;
    
    @GetMapping("/potential")
    public ResponseEntity<List<UserResponseDto>> getPotentialMatches() {
        return ResponseEntity.ok(matchingService.getPotentialMatches());
    }
    
    @PostMapping("/swipe")
    public ResponseEntity<SwipeResponseDto> swipe(@Valid @RequestBody SwipeRequestDto request) {
        return ResponseEntity.ok(matchingService.swipe(request));
    }
    
    @GetMapping("/likes")
    public ResponseEntity<List<UserResponseDto>> getLikes() {
        return ResponseEntity.ok(matchingService.getLikes());
    }
}
