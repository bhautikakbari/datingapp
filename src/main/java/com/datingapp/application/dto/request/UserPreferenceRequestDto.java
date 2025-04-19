package com.datingapp.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequestDto {
    
    @NotNull(message = "Minimum age is required")
    @Min(value = 18, message = "Minimum age must be at least 18")
    @Max(value = 100, message = "Minimum age must be at most 100")
    private Integer minAge;
    
    @NotNull(message = "Maximum age is required")
    @Min(value = 18, message = "Maximum age must be at least 18")
    @Max(value = 100, message = "Maximum age must be at most 100")
    private Integer maxAge;
    
    @NotNull(message = "Maximum distance is required")
    @Min(value = 1, message = "Maximum distance must be at least 1")
    @Max(value = 500, message = "Maximum distance must be at most 500")
    private Integer maxDistance;
}
