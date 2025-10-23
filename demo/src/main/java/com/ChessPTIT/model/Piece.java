package com.ChessPTIT.model;

public class Piece {
    protected final PieceColor color;

    public Piece(PieceColor color) {
        this.color = color;
    }

    public PieceColor getColor() {
        return color;
    }
}