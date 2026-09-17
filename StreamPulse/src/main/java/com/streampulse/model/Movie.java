package com.streampulse.model;

import java.util.Map;

public class Movie extends MediaItem {
    private int durationMinutes;
    private String director;

    public Movie(String id, String title, int releaseYear, double rating, Map<String, Double> genres, int durationMinutes, String director) {
        super(id, title, releaseYear, rating, genres);
        this.durationMinutes = durationMinutes;
        this.director = director;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    @Override
    public String getDisplaySummary() {
        return String.format("[MOVIE] %s (%d) | %.1f | Directed by: %s | %d min",
                getTitle(), getReleaseYear(), getRating(), director, durationMinutes);
    }
}
