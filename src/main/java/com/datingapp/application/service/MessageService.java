package com.datingapp.application.service;

import com.datingapp.application.dto.request.MessageRequestDto;
import com.datingapp.application.dto.response.MatchResponseDto;
import com.datingapp.application.dto.response.MessageResponseDto;
import com.datingapp.application.exception.BadRequestException;
import com.datingapp.application.exception.ResourceNotFoundException;
import com.datingapp.application.mapper.MatchMapper;
import com.datingapp.application.mapper.MessageMapper;
import com.datingapp.domain.entity.Match;
import com.datingapp.domain.entity.Message;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.repository.MatchRepository;
import com.datingapp.domain.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    @Autowired
    public MatchRepository matchRepository;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public AuthService authService;
    @Autowired
    public MatchMapper matchMapper;
    @Autowired
    public MessageMapper messageMapper;
    
    public List<MatchResponseDto> getMatches() {
        User currentUser = authService.getCurrentUser();
        List<Match> matches = matchRepository.findMatchesByUserId(currentUser.getId());
        
        return matches.stream()
                .map(match -> matchMapper.toResponseDto(match, currentUser))
                .collect(Collectors.toList());
    }
    
    public List<MessageResponseDto> getMessages(Long matchId) {
        User currentUser = authService.getCurrentUser();
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        
        // Verify user is part of this match
        if (!match.getUser1().getId().equals(currentUser.getId()) && 
                !match.getUser2().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not part of this match");
        }
        
        // Mark messages as read
        List<Message> unreadMessages = match.getMessages().stream()
                .filter(m -> !m.getSender().getId().equals(currentUser.getId()) && !m.isRead())
                .collect(Collectors.toList());
        
        if (!unreadMessages.isEmpty()) {
            unreadMessages.forEach(m -> m.setRead(true));
            messageRepository.saveAll(unreadMessages);
        }
        
        List<Message> messages = messageRepository.findByMatchIdOrderByCreatedAtAsc(matchId);
        
        return messages.stream()
                .map(message -> messageMapper.toResponseDto(message, currentUser))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MessageResponseDto sendMessage(MessageRequestDto request) {
        User currentUser = authService.getCurrentUser();
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        
        // Verify user is part of this match
        if (!match.getUser1().getId().equals(currentUser.getId()) && 
                !match.getUser2().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not part of this match");
        }
        
        Message message = Message.builder()
                .match(match)
                .sender(currentUser)
                .content(request.getContent())
                .build();
        
        message = messageRepository.save(message);
        
        return messageMapper.toResponseDto(message, currentUser);
    }
}
