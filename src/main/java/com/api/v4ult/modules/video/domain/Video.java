package com.api.v4ult.modules.video.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "videos")
public class Video {

    @Id
    private String id;

    private String title;
    private String description;
    private String videoUrl;

    @Builder.Default
    private Long views = 0L;

    private String authorId;
    private String authorName;

    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    private Instant createdAt = Instant.now();

}
