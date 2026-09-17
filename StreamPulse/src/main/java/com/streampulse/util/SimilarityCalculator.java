package com.streampulse.util;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class SimilarityCalculator {

    /**
     * Computes the Cosine Similarity between two genre weight vectors.
     * @param vecA First vector
     * @param vecB Second vector
     * @return Similarity score between 0.0 and 1.0. Returns 0.0 if either vector is empty or has zero magnitude.
     */
    public static double computeCosineSimilarity(Map<String, Double> vecA, Map<String, Double> vecB) {
        if (vecA == null || vecB == null || vecA.isEmpty() || vecB.isEmpty()) {
            return 0.0;
        }

        Set<String> allGenres = new HashSet<>(vecA.keySet());
        allGenres.addAll(vecB.keySet());

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String genre : allGenres) {
            double valA = vecA.getOrDefault(genre, 0.0);
            double valB = vecB.getOrDefault(genre, 0.0);

            dotProduct += valA * valB;
            normA += valA * valA;
            normB += valB * valB;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
