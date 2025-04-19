package com.datingapp.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDto {
    private Long id;
    private UserResponseDto matchedUser;
    private LocalDateTime matchedAt;
    private MessageResponseDto lastMessage;
    private long unreadCount;
}
