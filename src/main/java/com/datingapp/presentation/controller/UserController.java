package com.datingapp.presentation.controller;

import com.datingapp.application.dto.request.ProfileUpdateRequestDto;
import com.datingapp.application.dto.request.UserPreferenceRequestDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.application.service.PhotoService;
import com.datingapp.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    @Autowired
    public UserService userService;
    @Autowired
    public PhotoService photoService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateProfile(@Valid @RequestBody ProfileUpdateRequestDto request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
    
    @PutMapping("/preferences")
    public ResponseEntity<UserResponseDto> updatePreferences(@Valid @RequestBody UserPreferenceRequestDto preferences) {
        return ResponseEntity.ok(userService.updatePreferences(preferences));
    }
    
    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> uploadPhoto(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(photoService.uploadPhoto(file));
    }
    
    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<UserResponseDto> deletePhoto(@PathVariable Long photoId) {
        return ResponseEntity.ok(photoService.deletePhoto(photoId));
    }
    
    @PutMapping("/photos/{photoId}/primary")
    public ResponseEntity<UserResponseDto> setPrimaryPhoto(@PathVariable Long photoId) {
        return ResponseEntity.ok(photoService.setPrimaryPhoto(photoId));
    }
}
