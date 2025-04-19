package com.datingapp.domain.repository;

import com.datingapp.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByMatchIdOrderByCreatedAtAsc(Long matchId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.match.id = :matchId AND m.sender.id != :userId AND m.read = false")
    long countUnreadMessagesInMatch(@Param("matchId") Long matchId, @Param("userId") Long userId);
}
