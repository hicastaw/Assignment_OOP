package com.ChessPTIT.model;

public class King extends Piece {
    private boolean hasMoved = false;

    public King(PieceColor color) {
        super(color);
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}