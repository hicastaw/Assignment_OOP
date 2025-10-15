package com.ChessPTIT.model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    // --- BƯỚC 1: THÊM THUỘC TÍNH NÀY VÀO ---
    private boolean hasMoved = false;

    public King(PieceColor color) {
        super(color);
    }

    @Override
    public List<Position> getValidMoves(Board board, Position currentPosition) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        int[][] all_directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] dir : all_directions) {
            int nextRow = currentRow + dir[0];
            int nextCol = currentCol + dir[1];
            Position nextPos = new Position(nextRow, nextCol);

            if (isValid(nextPos)) {
                Piece pieceAtNextPos = board.getPieceAt(nextPos);
                if (pieceAtNextPos == null || pieceAtNextPos.getColor() != this.getColor()) {
                    moves.add(nextPos);
                }
            }
        }

        return moves;
    }

    private boolean isValid(Position pos) {
        return pos.row() >= 0 && pos.row() < 8 && pos.col() >= 0 && pos.col() < 8;
    }

    // --- BƯỚC 2: THÊM CÁC PHƯƠNG THỨC GETTER/SETTER NÀY VÀO ---
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}