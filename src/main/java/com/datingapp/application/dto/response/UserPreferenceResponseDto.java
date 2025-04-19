package com.datingapp.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceResponseDto {
    private Integer minAge;
    private Integer maxAge;
    private Integer maxDistance;
}
