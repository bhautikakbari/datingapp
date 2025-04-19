package com.datingapp.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {
    
    @NotNull(message = "Match ID is required")
    private Long matchId;
    
    @NotBlank(message = "Content is required")
    private String content;
}
