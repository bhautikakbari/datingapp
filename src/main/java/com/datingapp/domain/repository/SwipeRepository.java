package com.datingapp.domain.repository;

import com.datingapp.domain.entity.Swipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    
    @Query("SELECT COUNT(s) FROM Swipe s WHERE s.swiper.id = :userId AND s.createdAt >= :since")
    long countSwipesSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    Optional<Swipe> findBySwipedIdAndSwiperId(Long swipedId, Long swiperId);
    
    @Query("SELECT s FROM Swipe s WHERE s.swiped.id = :userId AND s.direction = 'RIGHT' AND s.swiper.id NOT IN " + "(SELECT sw.swiped.id FROM Swipe sw WHERE sw.swiper.id = :userId)")
    List<Swipe> findPendingLikesForUser(@Param("userId") Long userId);
}
