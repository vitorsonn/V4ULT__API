package com.api.v4ult.modules.video.dto;

import com.api.v4ult.modules.video.domain.Comment;
import com.api.v4ult.modules.video.domain.Video;

import java.time.Instant;
import java.util.List;

public record VideoResponseDTO(
        String id,
        String title,
        String description,
        String videoUrl,
        Long views,
        String authorId,
        String authorName,
        List<Comment> comments,
        Instant createdAt
) {

    public static VideoResponseDTO fromEntity(Video video) {
        return new VideoResponseDTO(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getVideoUrl(),
                video.getViews(),
                video.getAuthorId(),
                video.getAuthorName(),
                video.getComments(),
                video.getCreatedAt()
        );
    }
}




