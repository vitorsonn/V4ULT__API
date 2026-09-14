package com.api.v4ult.modules.video.service;


import com.api.v4ult.modules.video.domain.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoViewsBatchJob {

    private final StringRedisTemplate stringRedisTemplate;
    private final MongoTemplate mongoTemplate;

    private static final String VIEWS_BUFFER_KEY_PATTERN = "video:views:buffer:*";
    private static final String VIEWS_BUFFER_PREFIX = "video:views:buffer:";

    @Scheduled(fixedDelay = 10000)
    public void syncViewsToDatabase(){
        Set<String> keys = stringRedisTemplate.keys(VIEWS_BUFFER_KEY_PATTERN);

        if (keys ==  null || keys.isEmpty()) {
            log.info("No video views to sync.");
            return;
        }

        log.info("SINCRONIZANDO VIEWS EM BATCH PARA {} VIDEOS", keys.size());

        for(String key: keys){
            String videoId = key.replace(VIEWS_BUFFER_PREFIX, "");

            String viewsCountStr = stringRedisTemplate.opsForValue().getAndDelete(key);

            if (viewsCountStr != null && !viewsCountStr.isEmpty()) {
                long viewsToAdd = Long.parseLong(viewsCountStr);

                if (viewsToAdd > 0) {
                    Query query = new Query(Criteria.where("_id").is(new ObjectId(videoId)));
                    Update update = new Update().inc("views", viewsToAdd);
                    mongoTemplate.updateFirst(query, update, Video.class);
                    log.info("Updated video {} with {} new views.", videoId, viewsToAdd);
                }
            }
        }

    }


}
