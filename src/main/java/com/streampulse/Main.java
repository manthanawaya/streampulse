package com.streampulse;

import com.streampulse.exception.InvalidDataException;
import com.streampulse.exception.MediaNotFoundException;
import com.streampulse.model.MediaItem;
import com.streampulse.model.Movie;
import com.streampulse.model.Series;
import com.streampulse.model.UserProfile;
import com.streampulse.repository.DatabaseManager;
import com.streampulse.repository.FileMediaRepository;
import com.streampulse.repository.MediaRepository;
import com.streampulse.service.AnalyticsService;
import com.streampulse.service.CatalogService;
import com.streampulse.service.RecommendationService;
import com.streampulse.service.RecommendationService.ScoredMedia;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserProfile currentUser;
    private static CatalogService catalogService;
    private static RecommendationService recommendationService;
    private static AnalyticsService analyticsService;
    private static DatabaseManager dbManager;

    public static void main(String[] args) {
        System.out.println("Welcome to StreamPulse!");
        System.out.print("Enter your username to login/register: ");
        String username = scanner.nextLine().trim();
        currentUser = new UserProfile(username);

        try {
            MediaRepository fileRepo = new FileMediaRepository("data/catalog_seed.csv");
            catalogService = new CatalogService(fileRepo);
            recommendationService = new RecommendationService(catalogService);
            analyticsService = new AnalyticsService(catalogService);
            dbManager = DatabaseManager.getInstance();

            // Load existing watchlist from DB
            List<String> watchlistIds = dbManager.getWatchlistIdsForUser(username);
            for (String id : watchlistIds) {
                try {
                    currentUser.addToWatchlist(catalogService.getMediaById(id));
                } catch (MediaNotFoundException e) {
                    // Item in DB but not in catalog, ignore
                }
            }

        } catch (InvalidDataException e) {
            System.err.println("Failed to initialize catalog: " + e.getMessage());
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    browseCatalog();
                    break;
                case "2":
                    searchOrFilter();
                    break;
                case "3":
                    manageWatchlist();
                    break;
                case "4":
                    rateMedia();
                    break;
                case "5":
                    generateRecommendations();
                    break;
                case "6":
                    viewAnalytics();
                    break;
                case "7":
                    running = false;
                    System.out.println("Exiting StreamPulse. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Browse Full Catalog");
        System.out.println("2. Search / Filter Catalog");
        System.out.println("3. View & Manage My Watchlist");
        System.out.println("4. Rate a Movie/Series");
        System.out.println("5. Generate Top Recommendations");
        System.out.println("6. View Personal Analytics");
        System.out.println("7. Exit");
    }

    private static void browseCatalog() {
        System.out.println("\n--- Full Catalog ---");
        List<MediaItem> allMedia = catalogService.getAllMedia();
        allMedia.forEach(item -> System.out.println(item.getId() + ": " + item.getDisplaySummary()));
    }

    private static void searchOrFilter() {
        System.out.println("\n--- Search / Filter ---");
        System.out.println("1. Search by Keyword");
        System.out.println("2. Filter by Type (Movie/Series)");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Enter keyword: ");
            String keyword = scanner.nextLine();
            List<MediaItem> results = catalogService.searchByTitle(keyword);
            if (results.isEmpty()) {
                System.out.println("No results found.");
            } else {
                results.forEach(item -> System.out.println(item.getId() + ": " + item.getDisplaySummary()));
            }
        } else if (choice.equals("2")) {
            System.out.print("Enter type (M for Movie, S for Series): ");
            String type = scanner.nextLine();
            List<MediaItem> results;
            if (type.equalsIgnoreCase("M")) {
                results = catalogService.filterByType(Movie.class);
            } else if (type.equalsIgnoreCase("S")) {
                results = catalogService.filterByType(Series.class);
            } else {
                System.out.println("Invalid type.");
                return;
            }
            results.forEach(item -> System.out.println(item.getId() + ": " + item.getDisplaySummary()));
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void manageWatchlist() {
        System.out.println("\n--- My Watchlist ---");
        if (currentUser.getWatchlist().isEmpty()) {
            System.out.println("Your watchlist is empty.");
        } else {
            currentUser.getWatchlist().forEach(item -> System.out.println(item.getId() + ": " + item.getDisplaySummary()));
        }

        System.out.println("\nOptions: [A]dd, [R]emove, [B]ack");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("A")) {
            System.out.print("Enter Media ID to add: ");
            String id = scanner.nextLine();
            try {
                MediaItem item = catalogService.getMediaById(id);
                if (currentUser.addToWatchlist(item)) {
                    dbManager.addWatchlistItem(currentUser.getUsername(), id);
                    System.out.println("Added to watchlist!");
                } else {
                    System.out.println("Item already in watchlist.");
                }
            } catch (MediaNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else if (choice.equalsIgnoreCase("R")) {
            System.out.print("Enter Media ID to remove: ");
            String id = scanner.nextLine();
            try {
                MediaItem item = catalogService.getMediaById(id);
                if (currentUser.removeFromWatchlist(item)) {
                    dbManager.removeWatchlistItem(currentUser.getUsername(), id);
                    System.out.println("Removed from watchlist.");
                } else {
                    System.out.println("Item not in watchlist.");
                }
            } catch (MediaNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void rateMedia() {
        System.out.print("\nEnter Media ID to rate: ");
        String id = scanner.nextLine();
        try {
            MediaItem item = catalogService.getMediaById(id);
            System.out.print("Enter rating (1.0 to 10.0): ");
            double rating = Double.parseDouble(scanner.nextLine());
            currentUser.rateMedia(id, rating);
            System.out.println("Rating saved for " + item.getTitle());
        } catch (MediaNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid rating format. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void generateRecommendations() {
        System.out.println("\n--- Top Recommendations ---");
        long startTime = System.currentTimeMillis();
        List<ScoredMedia> recommendations = recommendationService.generateRecommendations(currentUser, 5);
        long endTime = System.currentTimeMillis();

        if (recommendations.isEmpty()) {
            System.out.println("Not enough rating data to generate recommendations. Please rate some media items 7.0 or higher first.");
        } else {
            for (int i = 0; i < recommendations.size(); i++) {
                ScoredMedia sm = recommendations.get(i);
                System.out.printf("%d. %s - Match: %.1f%%\n", (i + 1), sm.item.getDisplaySummary(), sm.score * 100);
            }
            System.out.println("\n(Computation took " + (endTime - startTime) + " ms using multithreading)");
        }
    }

    private static void viewAnalytics() {
        System.out.println("\n--- Personal Analytics ---");
        int totalWatchTime = analyticsService.calculateTotalWatchDuration(currentUser);
        double avgRating = analyticsService.calculateAverageRating(currentUser);
        String favGenre = analyticsService.getFavoriteGenre(currentUser);

        System.out.println("Total Estimated Watch Time: " + totalWatchTime + " minutes");
        System.out.printf("Average Rating Given: %.2f\n", avgRating);
        System.out.println("Favorite Genre (highest rated): " + favGenre);
    }
}
