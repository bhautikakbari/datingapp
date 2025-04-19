package com.datingapp.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwipeResponseDto {
    private boolean isMatch;
    private Long matchId;
    private UserResponseDto matchedUser;
}
