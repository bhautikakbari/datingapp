package com.datingapp.application.service;

import com.datingapp.application.dto.response.UserResponseDto;
import com.datingapp.application.exception.BadRequestException;
import com.datingapp.application.exception.ResourceNotFoundException;
import com.datingapp.application.mapper.UserMapper;
import com.datingapp.domain.entity.User;
import com.datingapp.domain.entity.UserPhoto;
import com.datingapp.domain.repository.UserPhotoRepository;
import com.datingapp.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {
    
    @Autowired
    public UserPhotoRepository userPhotoRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public AuthService authService;

    @Autowired
    public UserMapper userMapper;
    
    @Value("${app.storage.location}")
    private String storageLocation;
    
    @Transactional
    public UserResponseDto uploadPhoto(MultipartFile file) {
        User user = authService.getCurrentUser();
        
        try {
            // Create storage directory if it doesn't exist
            Path uploadPath = Paths.get(storageLocation);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            
            // Create photo entity
            boolean isPrimary = user.getPhotos().isEmpty();
            UserPhoto photo = UserPhoto.builder()
                    .user(user)
                    .photoUrl("/uploads/" + filename)
                    .isPrimary(isPrimary)
                    .build();
            
            userPhotoRepository.save(photo);
            
            // Update profile completion status if needed
            if (!user.isProfileCompleted() && user.getBio() != null && !user.getBio().isEmpty() &&
                    user.getLocation() != null && !user.getLocation().isEmpty() &&
                    user.getLatitude() != null && user.getLongitude() != null) {
                user.setProfileCompleted(true);
                userRepository.save(user);
            }
            
            return userMapper.toResponseDto(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
    
    @Transactional
    public UserResponseDto deletePhoto(Long photoId) {
        User user = authService.getCurrentUser();
        
        UserPhoto photo = userPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
        
        if (!photo.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only delete your own photos");
        }
        
        // If deleting primary photo, set another one as primary if available
        if (photo.isPrimary() && user.getPhotos().size() > 1) {
            List<UserPhoto> otherPhotos = user.getPhotos().stream()
                    .filter(p -> !p.getId().equals(photoId))
                    .collect(Collectors.toList());
            
            if (!otherPhotos.isEmpty()) {
                UserPhoto newPrimary = otherPhotos.get(0);
                newPrimary.setPrimary(true);
                userPhotoRepository.save(newPrimary);
            }
        }
        
        // Delete file from storage
        try {
            String filename = photo.getPhotoUrl().substring(photo.getPhotoUrl().lastIndexOf('/') + 1);
            Path filePath = Paths.get(storageLocation).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but continue with database deletion
            System.err.println("Failed to delete file: " + e.getMessage());
        }
        
        userPhotoRepository.delete(photo);
        
        // Update profile completion status if needed
        if (user.getPhotos().isEmpty()) {
            user.setProfileCompleted(false);
            userRepository.save(user);
        }
        
        return userMapper.toResponseDto(user);
    }
    
    @Transactional
    public UserResponseDto setPrimaryPhoto(Long photoId) {
        User user = authService.getCurrentUser();
        
        UserPhoto photo = userPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
        
        if (!photo.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only set your own photos as primary");
        }
        
        // Reset primary flag for all other photos
        userPhotoRepository.resetPrimaryFlagExcept(user.getId(), photoId);
        
        // Set this photo as primary
        photo.setPrimary(true);
        userPhotoRepository.save(photo);
        
        return userMapper.toResponseDto(user);
    }
}
