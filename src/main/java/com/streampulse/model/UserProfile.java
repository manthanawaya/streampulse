package com.streampulse.model;

import java.util.HashMap;
import java.util.TreeSet;

public class UserProfile {
    private String username;
    private TreeSet<MediaItem> watchlist;
    private HashMap<String, Double> ratings;

    public UserProfile(String username) {
        this.username = username;
        // TreeSet uses the Comparable implementation in MediaItem
        this.watchlist = new TreeSet<>();
        this.ratings = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TreeSet<MediaItem> getWatchlist() {
        return watchlist;
    }

    public HashMap<String, Double> getRatings() {
        return ratings;
    }

    public boolean addToWatchlist(MediaItem item) {
        return watchlist.add(item);
    }

    public boolean removeFromWatchlist(MediaItem item) {
        return watchlist.remove(item);
    }

    public void rateMedia(String mediaId, double rating) {
        if (rating >= 1.0 && rating <= 10.0) {
            ratings.put(mediaId, rating);
        } else {
            throw new IllegalArgumentException("Rating must be between 1.0 and 10.0");
        }
    }
}
