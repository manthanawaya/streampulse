package com.streampulse.service;

import com.streampulse.model.MediaItem;
import com.streampulse.model.Movie;
import com.streampulse.model.Series;
import com.streampulse.model.UserProfile;
import com.streampulse.exception.MediaNotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnalyticsService {
    private CatalogService catalogService;

    public AnalyticsService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public int calculateTotalWatchDuration(UserProfile userProfile) {
        return userProfile.getRatings().keySet().stream()
                .mapToInt(id -> {
                    try {
                        MediaItem item = catalogService.getMediaById(id);
                        if (item instanceof Movie) {
                            return ((Movie) item).getDurationMinutes();
                        } else if (item instanceof Series) {
                            Series series = (Series) item;
                            return series.getEpisodesCount() * 45; // estimate 45 min per episode
                        }
                    } catch (MediaNotFoundException e) {
                        return 0;
                    }
                    return 0;
                })
                .sum();
    }

    public double calculateAverageRating(UserProfile userProfile) {
        return userProfile.getRatings().values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public String getFavoriteGenre(UserProfile userProfile) {
        Map<String, Double> genreScores = userProfile.getRatings().entrySet().stream()
                .flatMap(entry -> {
                    try {
                        MediaItem item = catalogService.getMediaById(entry.getKey());
                        return item.getGenres().entrySet().stream()
                                .map(genreEntry -> Map.entry(genreEntry.getKey(), genreEntry.getValue() * entry.getValue()));
                    } catch (MediaNotFoundException e) {
                        return java.util.stream.Stream.empty();
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum));

        Optional<Map.Entry<String, Double>> favorite = genreScores.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        return favorite.map(Map.Entry::getKey).orElse("None");
    }
}
