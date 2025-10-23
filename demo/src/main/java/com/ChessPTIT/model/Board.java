package com.ChessPTIT.model;

public class Board {
    private final Piece[][] squares;

    public Board() {
        this.squares = new Piece[8][8];
    }

    public Piece getPieceAt(Position pos) {
        return squares[pos.row()][pos.col()];
    }

    public void setPieceAt(Position pos, Piece piece) {
        squares[pos.row()][pos.col()] = piece;
    }

    public void movePiece(Position from, Position to) {
        Piece pieceToMove = getPieceAt(from);
        setPieceAt(to, pieceToMove);
        setPieceAt(from, null); // Ô cũ giờ trống
    }

    // Sắp xếp tất cả 32 quân cờ vào vị trí ban đầu của ván đấu
    public void setupBoard() {
        // Dọn sạch bàn cờ (để đảm bảo không có quân cờ cũ nào còn sót lại)
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = null;
            }
        }

        // Đặt cờ quân đen
        setPieceAt(new Position(0, 0), new Rook(PieceColor.BLACK));
        setPieceAt(new Position(0, 1), new Knight(PieceColor.BLACK));
        setPieceAt(new Position(0, 2), new Bishop(PieceColor.BLACK));
        setPieceAt(new Position(0, 3), new Queen(PieceColor.BLACK));
        setPieceAt(new Position(0, 4), new King(PieceColor.BLACK));
        setPieceAt(new Position(0, 5), new Bishop(PieceColor.BLACK));
        setPieceAt(new Position(0, 6), new Knight(PieceColor.BLACK));
        setPieceAt(new Position(0, 7), new Rook(PieceColor.BLACK));
        for (int c = 0; c < 8; c++) {
            setPieceAt(new Position(1, c), new Pawn(PieceColor.BLACK));
        }

        // Đặt cờ quân trắng
        for (int c = 0; c < 8; c++) {
            setPieceAt(new Position(6, c), new Pawn(PieceColor.WHITE));
        }
        setPieceAt(new Position(7, 0), new Rook(PieceColor.WHITE));
        setPieceAt(new Position(7, 1), new Knight(PieceColor.WHITE));
        setPieceAt(new Position(7, 2), new Bishop(PieceColor.WHITE));
        setPieceAt(new Position(7, 3), new Queen(PieceColor.WHITE));
        setPieceAt(new Position(7, 4), new King(PieceColor.WHITE));
        setPieceAt(new Position(7, 5), new Bishop(PieceColor.WHITE));
        setPieceAt(new Position(7, 6), new Knight(PieceColor.WHITE));
        setPieceAt(new Position(7, 7), new Rook(PieceColor.WHITE));
    }

    // Tạo ra một bản sao (clone) của đối tượng Board hiện tại
    // -> Thử nước đi trên bàn cờ tạm mà không làm ảnh hưởng bàn cờ thật
    public Board deepClone() {
        Board newBoard = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                // Sao chép từng quân cờ sang bàn cờ mới
                newBoard.squares[r][c] = this.squares[r][c];
            }
        }
        return newBoard;
    }
}