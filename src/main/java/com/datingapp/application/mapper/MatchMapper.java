package com.datingapp.application.mapper;

import com.datingapp.application.dto.response.MatchResponseDto;
import com.datingapp.application.dto.response.MessageResponseDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.domain.entity.Match;
import com.datingapp.domain.entity.Message;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
@RequiredArgsConstructor
public class MatchMapper {
    
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public MessageMapper messageMapper;
    @Autowired
    public MessageRepository messageRepository;
    
    public MatchResponseDto toResponseDto(Match match, User currentUser) {
        User matchedUser = match.getUser1().getId().equals(currentUser.getId()) 
                ? match.getUser2() 
                : match.getUser1();
        
        UserResponseDto matchedUserDto = userMapper.toResponseDto(matchedUser);
        
        // Get last message if any
        MessageResponseDto lastMessageDto = null;
        if (!match.getMessages().isEmpty()) {
            Message lastMessage = match.getMessages().stream()
                    .max(Comparator.comparing(Message::getCreatedAt))
                    .orElse(null);
            
            if (lastMessage != null) {
                lastMessageDto = messageMapper.toResponseDto(lastMessage, currentUser);
            }
        }
        
        // Count unread messages
        long unreadCount = messageRepository.countUnreadMessagesInMatch(match.getId(), currentUser.getId());
        
        return MatchResponseDto.builder()
                .id(match.getId())
                .matchedUser(matchedUserDto)
                .matchedAt(match.getMatchedAt())
                .lastMessage(lastMessageDto)
                .unreadCount(unreadCount)
                .build();
    }
}
