package com.api.v4ult.modules.video.controller;


import com.api.v4ult.modules.video.dto.CreateCommentDTO;
import com.api.v4ult.modules.video.dto.CreateVideoDTO;
import com.api.v4ult.modules.video.dto.VideoResponseDTO;
import com.api.v4ult.modules.video.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;


    @PostMapping
    public ResponseEntity<VideoResponseDTO> createVideo(@RequestBody @Valid CreateVideoDTO dto) {
        VideoResponseDTO response = videoService.createVideo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDTO> findById(@PathVariable String id) {
        VideoResponseDTO response = videoService.findById(id);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> addComment(
            @PathVariable String id,
            @RequestBody @Valid CreateCommentDTO dto
    ) {
        videoService.addComment(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable String id) {
        videoService.incrementViews(id);
        return ResponseEntity.noContent().build();
    }
}
