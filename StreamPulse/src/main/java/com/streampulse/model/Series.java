package com.streampulse.model;

import java.util.Map;

public class Series extends MediaItem {
    private int seasons;
    private int episodesCount;

    public Series(String id, String title, int releaseYear, double rating, Map<String, Double> genres, int seasons, int episodesCount) {
        super(id, title, releaseYear, rating, genres);
        this.seasons = seasons;
        this.episodesCount = episodesCount;
    }

    public int getSeasons() {
        return seasons;
    }

    public void setSeasons(int seasons) {
        this.seasons = seasons;
    }

    public int getEpisodesCount() {
        return episodesCount;
    }

    public void setEpisodesCount(int episodesCount) {
        this.episodesCount = episodesCount;
    }

    @Override
    public String getDisplaySummary() {
        return String.format("[SERIES] %s (%d) | %.1f | %d Seasons, %d Episodes",
                getTitle(), getReleaseYear(), getRating(), seasons, episodesCount);
    }
}
