package com.example.SmartLibrary.repository;

import com.example.SmartLibrary.model.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    List<Notification> findAllSorted();

    // Get notifications for user (both user-specific and broadcast)
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId OR n.userId IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdOrBroadcast(@Param("userId") Long userId);

    // Get unread notifications for user
    @Query("SELECT n FROM Notification n WHERE (n.userId = :userId OR n.userId IS NULL) AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserIdOrBroadcast(@Param("userId") Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
    int markAsRead(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId OR n.userId IS NULL")
    int markAllAsReadForUser(Long userId);

    void deleteAllByUserId(Long userId);
}