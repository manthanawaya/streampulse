package com.streampulse.service;

import com.streampulse.exception.MediaNotFoundException;
import com.streampulse.model.MediaItem;
import com.streampulse.repository.MediaRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CatalogService {
    private MediaRepository repository;

    public CatalogService(MediaRepository repository) {
        this.repository = repository;
    }

    public List<MediaItem> getAllMedia() {
        return repository.findAll();
    }

    public MediaItem getMediaById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Media item with ID " + id + " not found."));
    }

    public List<MediaItem> searchByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        String lowerKeyword = keyword.toLowerCase();
        return repository.findAll().stream()
                .filter(item -> item.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<MediaItem> filterByType(Class<? extends MediaItem> type) {
        return repository.findAll().stream()
                .filter(type::isInstance)
                .collect(Collectors.toList());
    }
}
