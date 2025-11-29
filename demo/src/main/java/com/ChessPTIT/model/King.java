package com.ChessPTIT.model;

import com.ChessPTIT.service.GameService;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    private boolean hasMoved = false;

    public King(PieceColor color) {
        super(color);
    }

    @Override
    public List<Position> getValidMoves(Board board, Position currentPosition, GameService gameService) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        // 1. Tám nước đi một ô cơ bản
        int[][] all_directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };
        for (int[] dir : all_directions) {
            Position nextPos = new Position(currentRow + dir[0], currentCol + dir[1]);
            if (isValid(nextPos)) {
                Piece pieceAtNextPos = board.getPieceAt(nextPos);
                if (pieceAtNextPos == null || pieceAtNextPos.getColor() != this.getColor()) {
                    moves.add(nextPos);
                }
            }
        }

        // 2. LOGIC TẠO NƯỚC ĐI NHẬP THÀNH
        if (!hasMoved()) { // Điều kiện 1: Vua chưa di chuyển
            // Nhập thành cánh Vua (bên phải)
            Piece kingSideRook = board.getPieceAt(new Position(currentRow, 7));
            if (kingSideRook instanceof Rook && !((Rook) kingSideRook).hasMoved()) {
                // Điều kiện 2 & 3: Xe chưa di chuyển và các ô ở giữa trống
                if (board.getPieceAt(new Position(currentRow, 5)) == null &&
                        board.getPieceAt(new Position(currentRow, 6)) == null) {
                    moves.add(new Position(currentRow, 6)); // Thêm nước đi 2 ô của Vua
                }
            }

            // Nhập thành cánh Hậu (bên trái)
            Piece queenSideRook = board.getPieceAt(new Position(currentRow, 0));
            if (queenSideRook instanceof Rook && !((Rook) queenSideRook).hasMoved()) {
                if (board.getPieceAt(new Position(currentRow, 1)) == null &&
                        board.getPieceAt(new Position(currentRow, 2)) == null &&
                        board.getPieceAt(new Position(currentRow, 3)) == null) {
                    moves.add(new Position(currentRow, 2));
                }
            }
        }

        return moves;
    }

    private boolean isValid(Position pos) {
        return pos.row() >= 0 && pos.row() < 8 && pos.col() >= 0 && pos.col() < 8;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}