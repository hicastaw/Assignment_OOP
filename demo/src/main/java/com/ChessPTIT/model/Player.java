package com.ChessPTIT.model;

public class Player {
    private String name;
    private PieceColor color;

    public Player(String name, PieceColor color) {
        this.name = name;
        this.color = color;
    }

    // Getters
    public String getName() {
        return name;
    }

    public PieceColor getColor() {
        return color;
    }
}
