package com.datingapp.application.service;

import com.datingapp.application.dto.request.SwipeRequestDto;
import com.datingapp.application.dto.response.SwipeResponseDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.application.exception.BadRequestException;
import com.datingapp.application.exception.ResourceNotFoundException;
import com.datingapp.application.mapper.SwipeMapper;
import com.datingapp.application.mapper.UserMapper;
import com.datingapp.domain.entity.Match;
import com.datingapp.domain.entity.Swipe;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.repository.MatchRepository;
import com.datingapp.domain.repository.SwipeRepository;
import com.datingapp.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {
    
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public SwipeRepository swipeRepository;
    @Autowired
    public MatchRepository matchRepository;
    @Autowired
    public AuthService authService;
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public SwipeMapper swipeMapper;
    @Autowired
    public SubscriptionService subscriptionService;
    
    public List<UserResponseDto> getPotentialMatches() {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser.getPreferences() == null) {
            throw new BadRequestException("User preferences not set");
        }
        
        // Get subscription features to check daily swipe limit
        Map<String, Object> features = subscriptionService.getFeatures(currentUser.getId());
        Integer dailySwipes = (Integer) features.getOrDefault("daily_swipes", 10);
        
        // Check if user has reached daily swipe limit
        if (dailySwipes > 0) {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            long swipesToday = swipeRepository.countSwipesSince(currentUser.getId(), startOfDay);
            
            if (swipesToday >= dailySwipes) {
                throw new BadRequestException("Daily swipe limit reached");
            }
        }
        
        List<User> potentialMatches = userRepository.findPotentialMatches(
                currentUser.getId(),
                currentUser.getGender(),
                currentUser.getInterestedIn(),
                currentUser.getPreferences().getMinAge(),
                currentUser.getPreferences().getMaxAge(),
                currentUser.getLatitude(),
                currentUser.getLongitude(),
                currentUser.getPreferences().getMaxDistance()
        );
        
        return potentialMatches.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SwipeResponseDto swipe(SwipeRequestDto request) {
        User currentUser = authService.getCurrentUser();
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if already swiped
        if (swipeRepository.findBySwipedIdAndSwiperId(targetUser.getId(), currentUser.getId()).isPresent()) {
            throw new BadRequestException("Already swiped on this user");
        }
        
        // Create swipe record
        Swipe swipe = Swipe.builder()
                .swiper(currentUser)
                .swiped(targetUser)
                .direction(request.getDirection())
                .build();
        
        swipeRepository.save(swipe);
        
        // If right swipe, check for match
        if (request.getDirection() == Swipe.Direction.RIGHT) {
            Optional<Swipe> otherSwipe = swipeRepository.findBySwipedIdAndSwiperId(currentUser.getId(), targetUser.getId());
            
            if (otherSwipe.isPresent() && otherSwipe.get().getDirection() == Swipe.Direction.RIGHT) {
                // It's a match!
                Match match = Match.builder()
                        .user1(currentUser)
                        .user2(targetUser)
                        .build();
                
                match = matchRepository.save(match);
                
                return swipeMapper.toResponseDto(true, match, targetUser);
            }
        }
        
        return swipeMapper.toResponseDto(false, null, null);
    }
    
    public List<UserResponseDto> getLikes() {
        User currentUser = authService.getCurrentUser();
        
        // Check if user has premium subscription to see likes
        Map<String, Object> features = subscriptionService.getFeatures(currentUser.getId());
        Boolean canSeeLikes = (Boolean) features.getOrDefault("see_who_likes_you", false);
        
        if (!canSeeLikes) {
            throw new BadRequestException("Premium subscription required to see likes");
        }
        
        List<Swipe> pendingLikes = swipeRepository.findPendingLikesForUser(currentUser.getId());
        
        return pendingLikes.stream()
                .map(swipe -> userMapper.toResponseDto(swipe.getSwiper()))
                .collect(Collectors.toList());
    }
}
