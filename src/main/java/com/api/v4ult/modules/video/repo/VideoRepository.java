package com.api.v4ult.modules.video.repo;


import com.api.v4ult.modules.video.domain.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {



}
