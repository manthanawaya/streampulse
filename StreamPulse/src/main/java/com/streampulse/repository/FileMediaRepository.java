package com.streampulse.repository;

import com.streampulse.exception.InvalidDataException;
import com.streampulse.model.MediaItem;
import com.streampulse.model.Movie;
import com.streampulse.model.Series;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileMediaRepository implements MediaRepository {
    private List<MediaItem> catalog;

    public FileMediaRepository(String filePath) throws InvalidDataException {
        this.catalog = new ArrayList<>();
        loadData(filePath);
    }

    private void loadData(String filePath) throws InvalidDataException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            if (line == null) {
                throw new InvalidDataException("CSV file is empty");
            }

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 8) {
                    throw new InvalidDataException("Malformed line: " + line);
                }

                String id = parts[0].trim();
                String type = parts[1].trim();
                String title = parts[2].trim();
                int releaseYear = Integer.parseInt(parts[3].trim());
                double rating = Double.parseDouble(parts[4].trim());

                Map<String, Double> genres = parseGenres(parts[7].trim());

                if (type.equalsIgnoreCase("MOVIE")) {
                    int duration = Integer.parseInt(parts[5].trim());
                    String director = parts[6].trim();
                    catalog.add(new Movie(id, title, releaseYear, rating, genres, duration, director));
                } else if (type.equalsIgnoreCase("SERIES")) {
                    int seasons = Integer.parseInt(parts[5].trim());
                    int episodes = Integer.parseInt(parts[6].trim());
                    catalog.add(new Series(id, title, releaseYear, rating, genres, seasons, episodes));
                } else {
                    throw new InvalidDataException("Unknown media type: " + type);
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new InvalidDataException("Error reading data file: " + e.getMessage(), e);
        }
    }

    private Map<String, Double> parseGenres(String genreString) throws InvalidDataException {
        Map<String, Double> genres = new HashMap<>();
        String[] pairs = genreString.split(";");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length != 2) {
                throw new InvalidDataException("Invalid genre format: " + pair);
            }
            try {
                genres.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Invalid genre weight in: " + pair, e);
            }
        }
        return genres;
    }

    @Override
    public List<MediaItem> findAll() {
        return new ArrayList<>(catalog);
    }

    @Override
    public Optional<MediaItem> findById(String id) {
        return catalog.stream()
                .filter(item -> item.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public void save(MediaItem item) {
        // Find if it exists and replace, else add new
        Optional<MediaItem> existing = findById(item.getId());
        existing.ifPresent(catalog::remove);
        catalog.add(item);
    }
}
