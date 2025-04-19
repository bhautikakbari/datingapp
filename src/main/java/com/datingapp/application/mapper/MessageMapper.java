package com.datingapp.application.mapper;

import com.datingapp.application.dto.response.MessageResponseDto;
import com.datingapp.domain.entity.Message;
import com.datingapp.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    
    public MessageResponseDto toResponseDto(Message message, User currentUser) {
        boolean isMine = message.getSender().getId().equals(currentUser.getId());
        
        return MessageResponseDto.builder()
                .id(message.getId())
                .matchId(message.getMatch().getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .read(message.isRead())
                .createdAt(message.getCreatedAt())
                .isMine(isMine)
                .build();
    }
}
