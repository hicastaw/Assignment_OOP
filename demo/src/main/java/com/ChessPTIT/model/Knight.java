package com.ChessPTIT.model;

import com.ChessPTIT.service.GameService; 
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(PieceColor color) {
        super(color);
    }

    @Override
    public List<Position> getValidMoves(Board board, Position currentPosition, GameService gameService) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        // Định nghĩa 8 hướng di chuyển hình chữ L của quân Mã
        int[][] l_moves = {
                { -2, -1 }, { -2, 1 }, // Lên 2, trái/phải 1
                { -1, -2 }, { -1, 2 }, // Lên 1, trái/phải 2
                { 1, -2 }, { 1, 2 }, // Xuống 1, trái/phải 2
                { 2, -1 }, { 2, 1 } // Xuống 2, trái/phải 1
        };

        // Lặp qua tất cả 8 hướng đi có thể
        for (int[] move : l_moves) {
            int nextRow = currentRow + move[0];
            int nextCol = currentCol + move[1];
            Position nextPos = new Position(nextRow, nextCol);

            // 1. Kiểm tra xem ô đích có nằm trong bàn cờ không
            if (isValid(nextPos)) {
                Piece pieceAtNextPos = board.getPieceAt(nextPos);

                // 2. Kiểm tra ô đích:
                // - Hoặc là ô trống (null)
                // - Hoặc là có quân địch (màu khác)
                if (pieceAtNextPos == null || pieceAtNextPos.getColor() != this.getColor()) {
                    moves.add(nextPos);
                }
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