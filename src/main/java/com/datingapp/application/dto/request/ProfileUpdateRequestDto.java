package com.datingapp.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestDto {
    
    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;
    
    private String location;
    
    private Double latitude;
    
    private Double longitude;
}
