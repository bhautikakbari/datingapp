package com.datingapp.application.mapper;

import com.datingapp.application.dto.response.SwipeResponseDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.domain.entity.Match;
import com.datingapp.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SwipeMapper {
    
    private final UserMapper userMapper;
    
    public SwipeResponseDto toResponseDto(boolean isMatch, Match match, User matchedUser) {
        if (!isMatch) {
            return SwipeResponseDto.builder()
                    .isMatch(false)
                    .build();
        }
        
        UserResponseDto matchedUserDto = userMapper.toResponseDto(matchedUser);
        
        return SwipeResponseDto.builder()
                .isMatch(true)
                .matchId(match.getId())
                .matchedUser(matchedUserDto)
                .build();
    }
}
