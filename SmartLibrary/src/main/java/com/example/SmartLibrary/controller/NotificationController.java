package com.example.SmartLibrary.controller;

import com.example.SmartLibrary.model.Notification;
import com.example.SmartLibrary.service.NotificationBroadcastService;
import com.example.SmartLibrary.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;
    private final NotificationBroadcastService broadcastService;

    public NotificationController(NotificationService service, NotificationBroadcastService broadcastService) {
        this.service = service;
        this.broadcastService = broadcastService;
    }

    // SSE endpoint for real-time notifications
    @GetMapping("/stream/{userId}")
    public SseEmitter streamNotifications(@PathVariable Long userId) {
        return broadcastService.subscribe(userId);
    }

    // All notifications (admin view)
    @GetMapping
    public List<Notification> all() {
        return service.getAllNotificationsSorted();
    }

    // Notifications for a user
    @GetMapping("/user/{userId}")
    public List<Notification> forUser(@PathVariable Long userId) {
        return service.getUserNotifications(userId);
    }

    // Unread notifications for a user
    @GetMapping("/user/{userId}/unread")
    public List<Notification> unreadForUser(@PathVariable Long userId) {
        return service.getUnreadUserNotifications(userId);
    }

    // Unread count for a user
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> unreadCount(@PathVariable Long userId) {
        long count = service.getUnreadUserNotifications(userId).size();
        return ResponseEntity.ok(count);
    }

    // Mark single notification read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        boolean ok = service.markAsRead(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Mark all for user as read
    @PutMapping("/user/{userId}/read")
    public ResponseEntity<?> markAllRead(@PathVariable Long userId) {
        service.markAllAsReadForUser(userId);
        return ResponseEntity.ok().build();
    }

    // Admin book action notification
    @PostMapping("/admin/book-action")
    public ResponseEntity<?> createBookActionNotification(
            @RequestParam String action,
            @RequestParam String bookTitle,
            @RequestParam Long bookId,
            @RequestParam Long adminId) {

        service.notifyBookAction(action, bookTitle, bookId, adminId);
        return ResponseEntity.ok().build();
    }

    // Delete single
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // Delete all for user
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteAllForUser(@PathVariable Long userId) {
        service.deleteAllForUser(userId);
        return ResponseEntity.noContent().build();
    }
}