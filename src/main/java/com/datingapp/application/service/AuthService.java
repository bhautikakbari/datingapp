package com.datingapp.application.service;

import com.datingapp.application.dto.request.LoginRequestDto;
import com.datingapp.application.dto.request.RegisterRequestDto;
import com.datingapp.application.dto.response.AuthResponseDto;
import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.application.exception.ResourceAlreadyExistsException;
import com.datingapp.application.exception.UnauthorizedException;
import com.datingapp.application.mapper.UserMapper;
import com.datingapp.domain.entity.Role;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.entity.UserPreference;
import com.datingapp.domain.repository.RoleRepository;
import com.datingapp.domain.repository.UserRepository;
import com.datingapp.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public RoleRepository roleRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public JwtService jwtService;
    @Autowired
    public AuthenticationManager authenticationManager;
    @Autowired
    public  UserMapper userMapper;
    
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }
        
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(userRole));
        
        UserPreference preferences = UserPreference.builder()
                .user(user)
                .build();
        
        user.setPreferences(preferences);
        user = userRepository.save(user);
        
        String token = jwtService.generateToken(user);
        UserResponseDto userDto = userMapper.toResponseDto(user);
        
        return AuthResponseDto.builder()
                .token(token)
                .user(userDto)
                .build();
    }
    
    public AuthResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        UserResponseDto userDto = userMapper.toResponseDto(user);
        
        return AuthResponseDto.builder()
                .token(token)
                .user(userDto)
                .build();
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        
        return (User) authentication.getPrincipal();
    }
}
