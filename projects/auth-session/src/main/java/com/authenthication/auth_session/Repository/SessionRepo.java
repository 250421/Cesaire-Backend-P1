package com.authenthication.auth_session.Repository;

import com.authenthication.auth_session.Entity.SessionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepo extends JpaRepository<SessionInfo, Long> {
    Optional<SessionInfo> findBySessionId(String sessionId);
    List<SessionInfo> findByUser_UseridAndActiveTrue(Integer userId);
    void deleteByExpiresAtBefore(LocalDateTime now);
}