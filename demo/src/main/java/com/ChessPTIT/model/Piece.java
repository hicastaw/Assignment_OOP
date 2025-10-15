package com.ChessPTIT.model;

import java.util.List;

public abstract class Piece {
    protected final PieceColor color;

    public Piece(PieceColor color) {
        this.color = color;
    }

    public PieceColor getColor() {
        return color;
    }
    
    // Phương thức trừu tượng, buộc các lớp con phải định nghĩa logic di chuyển
    public abstract List<Position> getValidMoves(Board board, Position currentPosition);
}
