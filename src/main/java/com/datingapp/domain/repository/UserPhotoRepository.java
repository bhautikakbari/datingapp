package com.datingapp.domain.repository;

import com.datingapp.domain.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    
    List<UserPhoto> findByUserIdOrderByIsPrimaryDesc(Long userId);
    
    Optional<UserPhoto> findByUserIdAndIsPrimaryTrue(Long userId);
    
    @Modifying
    @Query("UPDATE UserPhoto p SET p.isPrimary = false WHERE p.user.id = :userId AND p.id != :photoId")
    void resetPrimaryFlagExcept(@Param("userId") Long userId, @Param("photoId") Long photoId);
}
