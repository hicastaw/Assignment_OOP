package com.ChessPTIT.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoveDAO {

    /**
     * Lưu một danh sách các nước đi vào database cho một ván đấu cụ thể.
     * Sử dụng batch updates để tăng hiệu suất.
     * 
     * @param matchId ID của ván đấu.
     * @param moves   Danh sách các nước đi dưới dạng chuỗi.
     */
    public void saveMovesForMatch(int matchId, List<String> moves) {
        String sql = "INSERT INTO moves (match_id, move_notation) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Bắt đầu một giao dịch

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (String move : moves) {
                    pstmt.setInt(1, matchId);
                    pstmt.setString(2, move);
                    pstmt.addBatch(); // Thêm lệnh vào một "lô"
                }
                pstmt.executeBatch(); // Thực thi tất cả các lệnh trong lô cùng một lúc
            }

            conn.commit(); // Xác nhận và ghi vĩnh viễn toàn bộ lô nước đi

        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu các nước đi. Đang rollback...");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hủy bỏ giao dịch nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Luôn đảm bảo đóng kết nối
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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

        // Đối với tác vụ chỉ đọc, try-with-resources là đủ và gọn gàng
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, matchId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(rs.getString("move_notation"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moves;
    }
}