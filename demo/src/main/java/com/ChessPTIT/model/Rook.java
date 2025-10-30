package com.ChessPTIT.model;

public class Rook extends Piece {
    private boolean hasMoved = false;

    public Rook(PieceColor color) {
        super(color);
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}