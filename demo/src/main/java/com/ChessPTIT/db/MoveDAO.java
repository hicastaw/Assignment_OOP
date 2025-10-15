package com.ChessPTIT.db;

import com.ChessPTIT.model.Match; // Giả sử bạn cần import Match, nếu không thì có thể bỏ
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoveDAO {

    public void saveMovesForMatch(int matchId, List<String> moves) {
        String sql = "INSERT INTO moves (match_id, move_notation) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String move : moves) {
                pstmt.setInt(1, matchId);
                pstmt.setString(2, move);
                pstmt.addBatch(); // Thêm lệnh vào một "lô"
            }
            pstmt.executeBatch(); // Thực thi tất cả các lệnh trong lô cùng lúc
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy tất cả các nước đi của một ván đấu cụ thể từ database.
     * 
     * @param matchId ID của ván đấu cần lấy nước đi.
     * @return Một danh sách các chuỗi, mỗi chuỗi là một ký hiệu nước đi.
     */
    public List<String> getMovesForMatch(int matchId) {
        String sql = "SELECT move_notation FROM moves WHERE match_id = ? ORDER BY move_id ASC";
        List<String> moves = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, matchId); // Gán giá trị cho tham số '?' trong câu lệnh SQL

            // Thực thi câu lệnh và nhận kết quả
            try (ResultSet rs = pstmt.executeQuery()) {
                // Lặp qua từng hàng trong kết quả trả về
                while (rs.next()) {
                    // Lấy giá trị từ cột "move_notation" và thêm vào danh sách
                    moves.add(rs.getString("move_notation"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moves; // Trả về danh sách các nước đi đã tìm thấy
    }
}