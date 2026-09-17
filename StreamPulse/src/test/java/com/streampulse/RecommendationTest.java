package com.streampulse;

import com.streampulse.exception.InvalidDataException;
import com.streampulse.model.MediaItem;
import com.streampulse.model.Movie;
import com.streampulse.model.UserProfile;
import com.streampulse.repository.MediaRepository;
import com.streampulse.service.CatalogService;
import com.streampulse.service.RecommendationService;
import com.streampulse.service.RecommendationService.ScoredMedia;
import com.streampulse.util.SimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationTest {

    private RecommendationService recommendationService;
    private CatalogService catalogService;
    private UserProfile testUser;

    // A dummy repository for testing
    static class DummyMediaRepository implements MediaRepository {
        private Map<String, MediaItem> items = new HashMap<>();

        public void addItem(MediaItem item) {
            items.put(item.getId(), item);
        }

        @Override
        public List<MediaItem> findAll() {
            return List.copyOf(items.values());
        }

        @Override
        public Optional<MediaItem> findById(String id) {
            return Optional.ofNullable(items.get(id));
        }

        @Override
        public void save(MediaItem item) {
            items.put(item.getId(), item);
        }
    }

    @BeforeEach
    void setUp() {
        DummyMediaRepository repo = new DummyMediaRepository();

        // Create some test items
        repo.addItem(new Movie("M1", "Sci-Fi Masterpiece", 2020, 8.5,
                Map.of("Sci-Fi", 1.0, "Action", 0.5), 120, "Director A"));

        repo.addItem(new Movie("M2", "Action Blast", 2021, 7.5,
                Map.of("Action", 1.0, "Thriller", 0.8), 100, "Director B"));

        repo.addItem(new Movie("M3", "Pure Drama", 2019, 8.0,
                Map.of("Drama", 1.0), 110, "Director C"));

        repo.addItem(new Movie("M4", "Sci-Fi Sequel", 2022, 9.0,
                Map.of("Sci-Fi", 0.9, "Action", 0.7), 130, "Director A"));


        catalogService = new CatalogService(repo);
        recommendationService = new RecommendationService(catalogService);
        testUser = new UserProfile("tester");
    }

    @Test
    void testComputeCosineSimilarity_IdenticalVectors() {
        Map<String, Double> vecA = Map.of("Sci-Fi", 1.0, "Action", 0.5);
        Map<String, Double> vecB = Map.of("Sci-Fi", 1.0, "Action", 0.5);

        double score = SimilarityCalculator.computeCosineSimilarity(vecA, vecB);
        assertEquals(1.0, score, 0.0001, "Identical vectors should have a similarity of 1.0");
    }

    @Test
    void testComputeCosineSimilarity_OrthogonalVectors() {
        Map<String, Double> vecA = Map.of("Sci-Fi", 1.0);
        Map<String, Double> vecB = Map.of("Comedy", 1.0);

        double score = SimilarityCalculator.computeCosineSimilarity(vecA, vecB);
        assertEquals(0.0, score, 0.0001, "Orthogonal vectors should have a similarity of 0.0");
    }

    @Test
    void testComputeCosineSimilarity_PartialMatch() {
        Map<String, Double> vecA = Map.of("Sci-Fi", 1.0, "Action", 0.5);
        Map<String, Double> vecB = Map.of("Action", 1.0, "Thriller", 0.5);

        double score = SimilarityCalculator.computeCosineSimilarity(vecA, vecB);
        assertTrue(score > 0.0 && score < 1.0, "Partial match should be between 0.0 and 1.0");
    }

    @Test
    void testRecommendationSorting() {
        // User rates M1 highly
        testUser.rateMedia("M1", 9.0);

        // M1 has genres: Sci-Fi: 1.0, Action: 0.5
        // User profile vector will be identical to M1's genres.

        // M4: Sci-Fi: 0.9, Action: 0.7 (Very similar)
        // M2: Action: 1.0, Thriller: 0.8 (Somewhat similar because of Action)
        // M3: Drama: 1.0 (Not similar at all)

        List<ScoredMedia> recommendations = recommendationService.generateRecommendations(testUser, 3);

        // Should return M4, then M2. M3 score should be 0 and might be excluded or at the bottom.
        assertEquals(2, recommendations.size(), "Should recommend 2 items with score > 0");

        assertEquals("M4", recommendations.get(0).item.getId(), "M4 should be top recommendation");
        assertEquals("M2", recommendations.get(1).item.getId(), "M2 should be second recommendation");

        // Verify sorting is descending
        assertTrue(recommendations.get(0).score >= recommendations.get(1).score,
                "Scores must be sorted in descending order");
    }
}
