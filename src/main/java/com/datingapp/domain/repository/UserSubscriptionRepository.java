package com.datingapp.domain.repository;

import com.datingapp.domain.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    
    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :userId AND s.endDate > :now ORDER BY s.endDate DESC")
    Optional<UserSubscription> findActiveSubscriptionByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
