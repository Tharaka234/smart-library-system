package com.example.SmartLibrary.service;

import com.example.SmartLibrary.model.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationBroadcastService {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<SseEmitter> broadcastEmitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(60_000L); // 60 seconds timeout

        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        broadcastEmitters.add(emitter);

        emitter.onCompletion(() -> {
            userEmitters.getOrDefault(userId, new CopyOnWriteArrayList<>()).remove(emitter);
            broadcastEmitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            userEmitters.getOrDefault(userId, new CopyOnWriteArrayList<>()).remove(emitter);
            broadcastEmitters.remove(emitter);
        });

        return emitter;
    }

    public void broadcast(Notification notification) {
        broadcastEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (IOException e) {
                broadcastEmitters.remove(emitter);
            }
        });
    }

    public void sendToUser(Long userId, Notification notification) {
        userEmitters.getOrDefault(userId, new CopyOnWriteArrayList<>()).forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (IOException e) {
                userEmitters.get(userId).remove(emitter);
            }
        });
    }
}