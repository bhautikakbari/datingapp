package com.datingapp.presentation.controller;

import com.datingapp.application.dto.request.MessageRequestDto;
import com.datingapp.application.dto.response.MatchResponseDto;
import com.datingapp.application.dto.response.MessageResponseDto;
import com.datingapp.application.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    @Autowired
    public MessageService messageService;
    
    @GetMapping("/matches")
    public ResponseEntity<List<MatchResponseDto>> getMatches() {
        return ResponseEntity.ok(messageService.getMatches());
    }
    
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<List<MessageResponseDto>> getMessages(@PathVariable Long matchId) {
        return ResponseEntity.ok(messageService.getMessages(matchId));
    }
    
    @PostMapping
    public ResponseEntity<MessageResponseDto> sendMessage(@Valid @RequestBody MessageRequestDto request) {
        return ResponseEntity.ok(messageService.sendMessage(request));
    }
}
