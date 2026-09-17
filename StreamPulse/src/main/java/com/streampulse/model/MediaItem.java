package com.streampulse.model;

import java.util.Map;
import java.util.Objects;

public abstract class MediaItem implements Comparable<MediaItem> {
    private String id;
    private String title;
    private int releaseYear;
    private double rating;
    private Map<String, Double> genres;

    public MediaItem(String id, String title, int releaseYear, double rating, Map<String, Double> genres) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.genres = genres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Map<String, Double> getGenres() {
        return genres;
    }

    public void setGenres(Map<String, Double> genres) {
        this.genres = genres;
    }

    public abstract String getDisplaySummary();

    @Override
    public int compareTo(MediaItem o) {
        int ratingCompare = Double.compare(o.getRating(), this.getRating());
        if (ratingCompare != 0) {
            return ratingCompare;
        }
        return this.getTitle().compareTo(o.getTitle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaItem mediaItem = (MediaItem) o;
        return Objects.equals(id, mediaItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
