package com.ChessPTIT.model;

import com.ChessPTIT.service.GameService; // <-- THÊM IMPORT NÀY
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(PieceColor color) {
        super(color);
    }

    @Override
    // THAY ĐỔI: Thêm GameService vào chữ ký phương thức
    public List<Position> getValidMoves(Board board, Position currentPosition, GameService gameService) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        // Định nghĩa 8 hướng đi của Hậu (4 hướng của Xe + 4 hướng của Tượng)
        int[][] directions = {
                { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, // Hướng của Xe
                { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } // Hướng của Tượng
        };

        // Đoạn code logic dưới đây giống hệt logic của quân Xe
        for (int[] dir : directions) {
            int nextRow = currentRow + dir[0];
            int nextCol = currentCol + dir[1];

            while (isValid(new Position(nextRow, nextCol))) {
                Position nextPos = new Position(nextRow, nextCol);
                Piece pieceAtNextPos = board.getPieceAt(nextPos);

                if (pieceAtNextPos == null) {
                    moves.add(nextPos);
                } else {
                    if (pieceAtNextPos.getColor() != this.getColor()) {
                        moves.add(nextPos);
                    }
                    break;
                }

                nextRow += dir[0];
                nextCol += dir[1];
            }
        }

        return moves;
    }

    private boolean isValid(Position pos) {
        return pos.row() >= 0 && pos.row() < 8 && pos.col() >= 0 && pos.col() < 8;
    }
}