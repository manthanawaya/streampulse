package com.streampulse.service;

import com.streampulse.exception.MediaNotFoundException;
import com.streampulse.model.MediaItem;
import com.streampulse.model.UserProfile;
import com.streampulse.util.SimilarityCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RecommendationService {
    private CatalogService catalogService;
    private static final int THREAD_COUNT = 4;

    public RecommendationService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public static class ScoredMedia implements Comparable<ScoredMedia> {
        public MediaItem item;
        public double score;

        public ScoredMedia(MediaItem item, double score) {
            this.item = item;
            this.score = score;
        }

        @Override
        public int compareTo(ScoredMedia o) {
            return Double.compare(o.score, this.score); // Descending
        }
    }

    public List<ScoredMedia> generateRecommendations(UserProfile user, int topN) {
        Map<String, Double> userProfileVector = buildUserProfileVector(user);
        if (userProfileVector.isEmpty()) {
            return Collections.emptyList(); // Not enough data to recommend
        }

        List<MediaItem> allMedia = catalogService.getAllMedia();
        List<MediaItem> unratedMedia = new ArrayList<>();
        
        for (MediaItem item : allMedia) {
            if (!user.getRatings().containsKey(item.getId())) {
                unratedMedia.add(item);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<ScoredMedia> allScored = new ArrayList<>();

        try {
            int chunkSize = (int) Math.ceil((double) unratedMedia.size() / THREAD_COUNT);
            List<Callable<List<ScoredMedia>>> tasks = new ArrayList<>();

            for (int i = 0; i < THREAD_COUNT; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, unratedMedia.size());
                if (start >= end) break;
                
                List<MediaItem> subList = unratedMedia.subList(start, end);
                tasks.add(() -> {
                    List<ScoredMedia> localScores = new ArrayList<>();
                    for (MediaItem item : subList) {
                        double score = SimilarityCalculator.computeCosineSimilarity(userProfileVector, item.getGenres());
                        if (score > 0) {
                            localScores.add(new ScoredMedia(item, score));
                        }
                    }
                    return localScores;
                });
            }

            List<Future<List<ScoredMedia>>> results = executor.invokeAll(tasks);
            for (Future<List<ScoredMedia>> future : results) {
                allScored.addAll(future.get());
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error generating recommendations: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        Collections.sort(allScored);
        return allScored.subList(0, Math.min(topN, allScored.size()));
    }

    private Map<String, Double> buildUserProfileVector(UserProfile user) {
        Map<String, Double> profileVector = new HashMap<>();
        Map<String, Double> userRatings = user.getRatings();

        for (Map.Entry<String, Double> entry : userRatings.entrySet()) {
            if (entry.getValue() >= 7.0) {
                try {
                    MediaItem item = catalogService.getMediaById(entry.getKey());
                    for (Map.Entry<String, Double> genreEntry : item.getGenres().entrySet()) {
                        String genre = genreEntry.getKey();
                        double weight = genreEntry.getValue();
                        profileVector.put(genre, profileVector.getOrDefault(genre, 0.0) + weight);
                    }
                } catch (MediaNotFoundException e) {
                    // Ignore missing items
                }
            }
        }
        return profileVector;
    }
}
