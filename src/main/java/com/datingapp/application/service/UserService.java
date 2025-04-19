package com.datingapp.application.service;

import com.datingapp.application.dto.request.ProfileUpdateRequestDto;
import com.datingapp.application.dto.request.UserPreferenceRequestDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.application.exception.ResourceNotFoundException;
import com.datingapp.application.mapper.UserMapper;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.entity.UserPreference;
import com.datingapp.domain.repository.UserPreferenceRepository;
import com.datingapp.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserPreferenceRepository userPreferenceRepository;
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public AuthService authService;
    
    public UserResponseDto getCurrentUser() {
        User user = authService.getCurrentUser();
        return userMapper.toResponseDto(user);
    }
    
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponseDto(user);
    }
    
    @Transactional
    public UserResponseDto updateProfile(ProfileUpdateRequestDto request) {
        User user = authService.getCurrentUser();
        
        userMapper.updateUserFromRequest(user, request);
        
        // Check if profile is completed
        boolean isProfileCompleted = user.getBio() != null && !user.getBio().isEmpty() &&
                user.getLocation() != null && !user.getLocation().isEmpty() &&
                user.getLatitude() != null && user.getLongitude() != null &&
                !user.getPhotos().isEmpty();
        
        user.setProfileCompleted(isProfileCompleted);
        user = userRepository.save(user);
        
        return userMapper.toResponseDto(user);
    }
    
    @Transactional
    public UserResponseDto updatePreferences(UserPreferenceRequestDto preferencesDto) {
        User user = authService.getCurrentUser();
        
        UserPreference preferences = userPreferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreference newPreferences = new UserPreference();
                    newPreferences.setUser(user);
                    return newPreferences;
                });
        
        userMapper.updatePreferenceFromRequest(preferences, preferencesDto);
        
        userPreferenceRepository.save(preferences);
        
        return userMapper.toResponseDto(user);
    }
}
