package com.api.v4ult.modules.video.service;


import com.api.v4ult.modules.video.domain.Comment;
import com.api.v4ult.modules.video.domain.Video;
import com.api.v4ult.modules.video.dto.CreateCommentDTO;
import com.api.v4ult.modules.video.dto.CreateVideoDTO;
import com.api.v4ult.modules.video.dto.PageResponseDTO;
import com.api.v4ult.modules.video.dto.VideoResponseDTO;
import com.api.v4ult.modules.video.repo.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final MongoTemplate mongoTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String VIEWS_BUFFER_KEY = "video:views:buffer:";


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

    @Cacheable(value = "videos", key = "#id")
    public VideoResponseDTO findById(String id) {
        System.out.println("=== BUSCANDO NO MONGODB (CACHE MISS) ===");
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado com o ID: " + id));
        return VideoResponseDTO.fromEntity(video);
    }

    @CacheEvict(value = "videos", key = "#videoId")
    public void addComment(String videoId, CreateCommentDTO dto) {

        Query query = new Query(Criteria.where("_id").is(new ObjectId(videoId)));

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
        String bufferKey = VIEWS_BUFFER_KEY + videoId;
        stringRedisTemplate.opsForValue().increment(bufferKey);
    }




    public PageResponseDTO<Comment> findCommentsPaginated(String videoId, int page, int size) {
        int pageNumber = Math.max(0, page);
        int pageSize = Math.max(1, size);
        int skip = pageNumber * pageSize;

        var matchStage = Aggregation.match(Criteria.where("_id").is(new ObjectId(videoId)));

        var projectStage = Aggregation.project()
                .and(context -> new Document("$slice", List.of("$comments", skip, pageSize)))
                .as("paginatedComments")
                .and("comments").size().as("totalComments");

        Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage);

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation,
                "videos",
                Document.class
        );

        Document resultDoc = results.getUniqueMappedResult();

        if (resultDoc == null) {
            throw new RuntimeException("Vídeo não encontrado para listar comentários");
        }

        List<Document> rawComments = resultDoc.getList("paginatedComments", Document.class);
        int totalElements = resultDoc.getInteger("totalComments", 0);

        List<Comment> comments = (rawComments != null) ? rawComments.stream()
                .map(doc -> {
                    String commentId = doc.getString("id") != null
                            ? doc.getString("id")
                            : doc.getString("_id");

                    return Comment.builder()
                            .id(commentId)
                            .userId(doc.getString("userId"))
                            .username(doc.getString("username"))
                            .text(doc.getString("text"))
                            .createdAt(doc.getDate("createdAt") != null ? doc.getDate("createdAt").toInstant() : null)
                            .build();
                })
                .toList() : List.of();
        return new PageResponseDTO<>(comments, page, size, totalElements);
    }

}
