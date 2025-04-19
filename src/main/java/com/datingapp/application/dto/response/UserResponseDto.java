package com.datingapp.application.dto.response;

import com.datingapp.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private int age;
    private User.Gender gender;
    private User.Gender interestedIn;
    private String bio;
    private String location;
    private Double latitude;
    private Double longitude;
    private boolean profileCompleted;
    private List<UserPhotoResponseDto> photos;
    private UserPreferenceResponseDto preferences;
    private SubscriptionResponseDto subscription;
}
