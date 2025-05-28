package com.mohit.chat.service.controller;

import com.mohit.chat.service.respository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> body) {
        String roomId = body.get("roomId");
        if (roomId == null || roomId.isBlank()) return ResponseEntity.badRequest().build();

        if (!roomRepository.roomExists(roomId)) {
            roomRepository.createRoom(roomId);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}/exists")
    public ResponseEntity<Boolean> checkRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(roomRepository.roomExists(roomId));
    }

}
