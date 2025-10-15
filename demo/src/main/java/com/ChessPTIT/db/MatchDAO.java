package com.ChessPTIT.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ChessPTIT.model.Match;

public class MatchDAO {

    public int createNewMatch(String whitePlayer, String blackPlayer) {
        String sql = "INSERT INTO matches (player_white_name, player_black_name, result) VALUES (?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, whitePlayer);
            pstmt.setString(2, blackPlayer);
            pstmt.setString(3, "In Progress"); // Trạng thái ban đầu

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public void updateMatchResult(int matchId, String result) {
        String sql = "UPDATE matches SET result = ? WHERE match_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, result);
            pstmt.setInt(2, matchId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thêm phương thức này vào lớp MatchDAO.java
    public List<Match> getAllMatches() {
        String sql = "SELECT * FROM matches ORDER BY match_id DESC";
        List<Match> matches = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                matches.add(new Match(
                        rs.getInt("match_id"),
                        rs.getString("player_white_name"),
                        rs.getString("player_black_name"),
                        rs.getString("result")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }
}