package com.api.v4ult.modules.video.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String userId;
    private String username;
    private String text;

    @Builder.Default
    private Instant createdAt = Instant.now();


}
