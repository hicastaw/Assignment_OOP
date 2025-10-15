package com.ChessPTIT.model;

public class Board {
    // Thuộc tính `squares` được đổi thành `public` để `deepClone` trong GameService
    // có thể truy cập
    // Hoặc giữ `private` và cung cấp một phương thức sao chép.
    // Để đơn giản, ta giữ nguyên code cũ và thêm phương thức clone vào đây.
    private final Piece[][] squares;

    public Board() {
        // Khởi tạo mảng 8x8
        this.squares = new Piece[8][8];
    }

    /**
     * Lấy quân cờ tại một vị trí cụ thể.
     * 
     * @param pos Vị trí cần lấy.
     * @return Đối tượng Piece tại vị trí đó, hoặc null nếu ô trống.
     */
    public Piece getPieceAt(Position pos) {
        return squares[pos.row()][pos.col()];
    }

    /**
     * Đặt một quân cờ vào một vị trí cụ thể.
     * 
     * @param pos   Vị trí cần đặt.
     * @param piece Quân cờ cần đặt vào.
     */
    public void setPieceAt(Position pos, Piece piece) {
        squares[pos.row()][pos.col()] = piece;
    }

    /**
     * Di chuyển một quân cờ từ vị trí cũ sang vị trí mới.
     * 
     * @param from Vị trí bắt đầu.
     * @param to   Vị trí kết thúc.
     */
    public void movePiece(Position from, Position to) {
        Piece pieceToMove = getPieceAt(from);
        setPieceAt(to, pieceToMove);
        setPieceAt(from, null); // Ô cũ giờ trống
    }

    /**
     * Sắp xếp tất cả 32 quân cờ vào vị trí ban đầu của ván đấu.
     */
    public void setupBoard() {
        // Dọn sạch bàn cờ (để đảm bảo không có quân cờ cũ nào còn sót lại)
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = null;
            }
        }

        // --- ĐẶT CÁC QUÂN CỜ ĐEN (BLACK) ---
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

        // --- ĐẶT CÁC QUÂN CỜ TRẮNG (WHITE) ---
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

    /**
     * Tạo ra một bản sao (clone) của đối tượng Board hiện tại.
     * Việc này cần thiết để "đi thử" một nước đi trên bàn cờ tạm mà không ảnh hưởng
     * đến bàn cờ thật của ván đấu.
     * 
     * @return Một đối tượng Board mới có trạng thái y hệt bàn cờ hiện tại.
     */
    public Board deepClone() {
        Board newBoard = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                // Sao chép từng quân cờ sang bàn cờ mới
                // Lưu ý: Đây vẫn là shallow copy của các đối tượng Piece,
                // nhưng đối với logic đi thử là đủ vì chúng ta không thay đổi trạng thái bên
                // trong của Piece.
                newBoard.squares[r][c] = this.squares[r][c];
            }
        }
        return newBoard;
    }
}