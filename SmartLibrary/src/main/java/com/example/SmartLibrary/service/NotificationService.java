package com.example.SmartLibrary.service;

import com.example.SmartLibrary.model.Notification;
import com.example.SmartLibrary.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationBroadcastService broadcastService;

    public NotificationService(NotificationRepository repo, NotificationBroadcastService broadcastService) {
        this.repo = repo;
        this.broadcastService = broadcastService;
    }

    // Create broadcast notification (for all users)
    @Transactional
    public Notification createBroadcastNotification(String title, String message, String type, String action, Long relatedBookId) {
        // Use the constructor that matches your Notification entity
        Notification notification = new Notification(title, message, type, action, relatedBookId);
        Notification saved = repo.save(notification);

        // Broadcast to all connected clients
        if (broadcastService != null) {
            broadcastService.broadcast(saved);
        }
        return saved;
    }

    // Create notification for specific user
    @Transactional
    public Notification createUserNotification(Long userId, String title, String message, String type, String action, Long relatedBookId) {
        // Use the constructor that matches your Notification entity
        Notification notification = new Notification(title, message, userId, type, action, relatedBookId);
        Notification saved = repo.save(notification);

        // Broadcast to specific user if connected
        if (broadcastService != null) {
            broadcastService.sendToUser(userId, saved);
        }
        return saved;
    }

    // Simple notification creation (for backward compatibility)
    @Transactional
    public Notification createNotification(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        // Set default values for new fields
        notification.setTitle("Notification");
        notification.setType("system");
        notification.setAction("info");
        return repo.save(notification);
    }

    // Simple user notification creation (for backward compatibility)
    @Transactional
    public Notification createNotificationForUser(Long userId, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        // Set default values for new fields
        notification.setTitle("Notification");
        notification.setType("system");
        notification.setAction("info");
        return repo.save(notification);
    }

    // Admin book actions - call this from your book service
    @Transactional
    public void notifyBookAction(String action, String bookTitle, Long bookId, Long adminId) {
        String title = "Book " + capitalize(action);
        String message = String.format("Book \"%s\" has been %s by administrator", bookTitle, action);

        createBroadcastNotification(title, message, "admin_action", action, bookId);
    }

    // Helper method to capitalize first letter
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public List<Notification> getAllNotificationsSorted() {
        return repo.findAllSorted();
    }

    public List<Notification> getUserNotifications(Long userId) {
        // Get both user-specific and broadcast notifications
        return repo.findByUserIdOrBroadcast(userId);
    }

    public List<Notification> getUnreadUserNotifications(Long userId) {
        return repo.findUnreadByUserIdOrBroadcast(userId);
    }

    // Get unread count for user
    public long getUnreadCountForUser(Long userId) {
        List<Notification> unread = repo.findUnreadByUserIdOrBroadcast(userId);
        return unread != null ? unread.size() : 0;
    }

    @Transactional
    public boolean markAsRead(Long id) {
        int updated = repo.markAsRead(id);
        return updated > 0;
    }

    @Transactional
    public boolean markAllAsReadForUser(Long userId) {
        int updated = repo.markAllAsReadForUser(userId);
        return updated > 0;
    }

    public void deleteNotification(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void deleteAllForUser(Long userId) {
        repo.deleteAllByUserId(userId);
    }

    public void sendNotificationToAllUsers(String s) {
    }
}