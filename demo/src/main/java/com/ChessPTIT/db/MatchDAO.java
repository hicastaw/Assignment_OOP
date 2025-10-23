package com.ChessPTIT.db;

import com.ChessPTIT.model.Match;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    /**
     * Tạo một ván đấu mới trong database và trả về ID của nó.
     * Sử dụng quản lý giao dịch thủ công để đảm bảo tính toàn vẹn dữ liệu.
     * 
     * @param whitePlayer Tên người chơi quân Trắng.
     * @param blackPlayer Tên người chơi quân Đen.
     * @return ID của ván đấu vừa được tạo, hoặc -1 nếu có lỗi.
     */
    public int createNewMatch(String whitePlayer, String blackPlayer) {
        String sql = "INSERT INTO matches (player_white_name, player_black_name, result) VALUES (?, ?, ?)";
        int generatedId = -1;
        Connection conn = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Bắt đầu một giao dịch

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, whitePlayer);
                pstmt.setString(2, blackPlayer);
                pstmt.setString(3, "In Progress");

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
            conn.commit(); // Xác nhận và lưu các thay đổi vĩnh viễn
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo ván đấu mới. Đang rollback...");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hủy bỏ mọi thay đổi nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Luôn đóng kết nối
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return generatedId;
    }

    /**
     * Cập nhật kết quả của một ván đấu đã có.
     * 
     * @param matchId ID của ván đấu.
     * @param result  Kết quả cuối cùng (ví dụ: "White wins by Checkmate").
     */
    public void updateMatchResult(int matchId, String result) {
        String sql = "UPDATE matches SET result = ? WHERE match_id = ?";
        Connection conn = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, result);
                pstmt.setInt(2, matchId);
                pstmt.executeUpdate();
            }

            conn.commit(); // Xác nhận và lưu thay đổi
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy danh sách tất cả các ván đấu đã được lưu trong database.
     * 
     * @return Một danh sách các đối tượng Match.
     */
    public List<Match> getAllMatches() {
        String sql = "SELECT * FROM matches ORDER BY match_id DESC";
        List<Match> matches = new ArrayList<>();

        // Đối với tác vụ chỉ đọc (SELECT), try-with-resources là đủ và gọn gàng nhất.
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