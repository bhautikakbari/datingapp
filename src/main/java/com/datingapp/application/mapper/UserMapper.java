package com.datingapp.application.mapper;

import com.datingapp.application.dto.request.ProfileUpdateRequestDto;
import com.datingapp.application.dto.request.RegisterRequestDto;
import com.datingapp.application.dto.request.UserPreferenceRequestDto;
import com.datingapp.application.dto.response.UserPhotoResponseDto;
import com.datingapp.application.dto.response.UserPreferenceResponseDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.entity.UserPhoto;
import com.datingapp.domain.entity.UserPreference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {
    
    private final SubscriptionMapper subscriptionMapper;
    
    public UserResponseDto toResponseDto(User user) {
        List<UserPhotoResponseDto> photoDtos = user.getPhotos().stream()
                .map(this::toPhotoResponseDto)
                .collect(Collectors.toList());
        
        UserPreferenceResponseDto preferencesDto = user.getPreferences() != null 
                ? toPreferenceResponseDto(user.getPreferences()) 
                : null;
        
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .age(calculateAge(user.getBirthDate()))
                .gender(user.getGender())
                .interestedIn(user.getInterestedIn())
                .bio(user.getBio())
                .location(user.getLocation())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .profileCompleted(user.isProfileCompleted())
                .photos(photoDtos)
                .preferences(preferencesDto)
                .subscription(user.getSubscriptions().isEmpty() ? null : 
                        subscriptionMapper.toResponseDto(user.getSubscriptions().get(0)))
                .build();
    }
    
    public User toEntity(RegisterRequestDto registerDto) {
        return User.builder()
                .email(registerDto.getEmail())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .birthDate(registerDto.getBirthDate())
                .gender(registerDto.getGender())
                .interestedIn(registerDto.getInterestedIn())
                .build();
    }
    
    public void updateUserFromRequest(User user, ProfileUpdateRequestDto requestDto) {
        if (requestDto.getBio() != null) {
            user.setBio(requestDto.getBio());
        }
        
        if (requestDto.getLocation() != null) {
            user.setLocation(requestDto.getLocation());
        }
        
        if (requestDto.getLatitude() != null && requestDto.getLongitude() != null) {
            user.setLatitude(requestDto.getLatitude());
            user.setLongitude(requestDto.getLongitude());
        }
    }
    
    public UserPhotoResponseDto toPhotoResponseDto(UserPhoto photo) {
        return UserPhotoResponseDto.builder()
                .id(photo.getId())
                .photoUrl(photo.getPhotoUrl())
                .isPrimary(photo.isPrimary())
                .build();
    }
    
    public UserPreferenceResponseDto toPreferenceResponseDto(UserPreference preference) {
        return UserPreferenceResponseDto.builder()
                .minAge(preference.getMinAge())
                .maxAge(preference.getMaxAge())
                .maxDistance(preference.getMaxDistance())
                .build();
    }
    
    public void updatePreferenceFromRequest(UserPreference preference, UserPreferenceRequestDto requestDto) {
        preference.setMinAge(requestDto.getMinAge());
        preference.setMaxAge(requestDto.getMaxAge());
        preference.setMaxDistance(requestDto.getMaxDistance());
    }
    
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
