package com.ChessPTIT.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {

        String createMatchesTable = """
                CREATE TABLE IF NOT EXISTS matches (
                    match_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_white_name TEXT NOT NULL,
                    player_black_name TEXT NOT NULL,
                    result TEXT NOT NULL
                );""";

        String createMovesTable = """
                CREATE TABLE IF NOT EXISTS moves (
                    move_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    match_id INTEGER NOT NULL,
                    move_notation TEXT NOT NULL,
                    FOREIGN KEY (match_id) REFERENCES matches (match_id)
                );""";

        try (Connection conn = DatabaseConnector.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createMatchesTable);
            stmt.execute(createMovesTable);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}