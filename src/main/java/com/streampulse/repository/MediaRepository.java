package com.streampulse.repository;

import com.streampulse.model.MediaItem;

import java.util.List;
import java.util.Optional;

public interface MediaRepository {
    List<MediaItem> findAll();
    Optional<MediaItem> findById(String id);
    void save(MediaItem item);
}
