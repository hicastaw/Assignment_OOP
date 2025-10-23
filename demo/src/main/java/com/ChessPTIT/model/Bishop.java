package com.ChessPTIT.model;

import com.ChessPTIT.service.GameService; // <-- THÊM IMPORT NÀY
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(PieceColor color) {
        super(color);
    }

    @Override
    // THAY ĐỔI: Thêm GameService vào chữ ký phương thức
    public List<Position> getValidMoves(Board board, Position currentPosition, GameService gameService) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        // Định nghĩa 4 hướng đi chéo của Tượng
        int[][] directions = {
                { -1, -1 }, { -1, 1 }, // Chéo lên (trái, phải)
                { 1, -1 }, { 1, 1 } // Chéo xuống (trái, phải)
        };

        // Đoạn code logic dưới đây giống hệt logic của quân Xe và Hậu
        for (int[] dir : directions) {
            int nextRow = currentRow + dir[0];
            int nextCol = currentCol + dir[1];

            while (isValid(new Position(nextRow, nextCol))) {
                Position nextPos = new Position(nextRow, nextCol);
                Piece pieceAtNextPos = board.getPieceAt(nextPos);

                if (pieceAtNextPos == null) {
                    // Nếu ô trống, đây là một nước đi hợp lệ
                    moves.add(nextPos);
                } else {
                    // Nếu có quân cờ ở ô tiếp theo
                    if (pieceAtNextPos.getColor() != this.getColor()) {
                        // Nếu là quân địch, có thể ăn.
                        moves.add(nextPos);
                    }
                    // Dù là quân địch hay quân ta, cũng phải dừng lại.
                    break;
                }

                // Di chuyển đến ô tiếp theo cùng hướng
                nextRow += dir[0];
                nextCol += dir[1];
            }
        }

        return moves;
    }

    /**
     * Phương thức pomocniczy để kiểm tra xem một vị trí có hợp lệ trên bàn cờ
     * không.
     */
    private boolean isValid(Position pos) {
        return pos.row() >= 0 && pos.row() < 8 && pos.col() >= 0 && pos.col() < 8;
    }
}