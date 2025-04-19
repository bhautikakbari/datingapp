package com.datingapp.domain.repository;

import com.datingapp.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query(value = """
            SELECT u FROM User u
            WHERE u.id != :userId
            AND u.gender = :interestedIn
            AND u.interestedIn = :gender
            AND u.id NOT IN (
                SELECT s.swiped.id FROM Swipe s WHERE s.swiper.id = :userId
            )
            AND FUNCTION('TIMESTAMPDIFF', YEAR, u.birthDate, CURRENT_DATE) BETWEEN :minAge AND :maxAge
            AND (:latitude IS NULL OR :longitude IS NULL OR 
                (6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * 
                cos(radians(u.longitude) - radians(:longitude)) + 
                sin(radians(:latitude)) * sin(radians(u.latitude)))) <= :maxDistance)
            ORDER BY u.profileCompleted DESC, FUNCTION('RAND')
            """)
    List<User> findPotentialMatches(
            @Param("userId") Long userId,
            @Param("gender") User.Gender gender,
            @Param("interestedIn") User.Gender interestedIn,
            @Param("minAge") int minAge,
            @Param("maxAge") int maxAge,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("maxDistance") int maxDistance);
}
