package com.streampulse.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private static final String URL = "jdbc:sqlite:streampulse.db";

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL);
            initializeTables();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeTables() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS watchlist (" +
                                "user_id TEXT, " +
                                "media_id TEXT, " +
                                "PRIMARY KEY(user_id, media_id));";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public void addWatchlistItem(String userId, String mediaId) {
        String sql = "INSERT OR IGNORE INTO watchlist(user_id, media_id) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, mediaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding to watchlist: " + e.getMessage());
        }
    }

    public void removeWatchlistItem(String userId, String mediaId) {
        String sql = "DELETE FROM watchlist WHERE user_id = ? AND media_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, mediaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing from watchlist: " + e.getMessage());
        }
    }

    public List<String> getWatchlistIdsForUser(String userId) {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT media_id FROM watchlist WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("media_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving watchlist: " + e.getMessage());
        }
        return ids;
    }
}
