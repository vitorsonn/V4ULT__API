package com.api.v4ult.modules.video.service;


import com.api.v4ult.modules.video.domain.Comment;
import com.api.v4ult.modules.video.domain.Video;
import com.api.v4ult.modules.video.dto.CreateCommentDTO;
import com.api.v4ult.modules.video.dto.CreateVideoDTO;
import com.api.v4ult.modules.video.dto.VideoResponseDTO;
import com.api.v4ult.modules.video.repo.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final MongoTemplate mongoTemplate;


    public VideoResponseDTO createVideo(CreateVideoDTO dto) {
        Video video = Video.builder()
                .title(dto.title())
                .description(dto.description())
                .videoUrl(dto.videoUrl())
                .authorId(dto.authorId())
                .authorName(dto.authorName())
                .build();
        Video savedVideo = videoRepository.save(video);
        return VideoResponseDTO.fromEntity(savedVideo);
    }

    public VideoResponseDTO findById(String id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado com o ID: " + id));
        return VideoResponseDTO.fromEntity(video);
    }

    public void addComment(String videoId, CreateCommentDTO dto) {

        Query query = new Query(Criteria.where("id").is(videoId));

        Comment comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .userId(dto.userId())
                .username(dto.username())
                .text(dto.text())
                .createdAt(Instant.now())
                .build();


        Update update = new Update().push("comments", comment);

        var result = mongoTemplate.updateFirst(query, update, Video.class);

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("Vídeo não encontrado para adicionar comentário");
        }
    }

    public void incrementViews(String videoId) {
        Query query = new Query(Criteria.where("id").is(videoId));

        Update update = new Update().inc("views", 1);

        var result = mongoTemplate.updateFirst(query, update, Video.class);

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("Vídeo não encontrado para incrementar visualizações");
        }
    }

}
