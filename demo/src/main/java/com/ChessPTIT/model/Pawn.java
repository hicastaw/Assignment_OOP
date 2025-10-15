package com.ChessPTIT.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color);
    }

    @Override
    public List<Position> getValidMoves(Board board, Position currentPosition) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        // Xác định hướng đi của Tốt. Quân Trắng đi lên (dòng giảm), quân Đen đi xuống (dòng tăng).
        int direction = (this.getColor() == PieceColor.WHITE) ? -1 : 1;

        // 1. KIỂM TRA NƯỚC ĐI THẲNG 1 Ô
        Position oneStepForward = new Position(currentRow + direction, currentCol);
        // Kiểm tra xem ô phía trước có nằm trong bàn cờ và có trống không
        if (isValid(oneStepForward) && board.getPieceAt(oneStepForward) == null) {
            moves.add(oneStepForward);

            // 2. KIỂM TRA NƯỚC ĐI THẲNG 2 Ô (CHỈ DÀNH CHO NƯỚC ĐẦU TIÊN)
            boolean isAtStartingRow = (this.getColor() == PieceColor.WHITE && currentRow == 6) ||
                                      (this.getColor() == PieceColor.BLACK && currentRow == 1);

            if (isAtStartingRow) {
                Position twoStepsForward = new Position(currentRow + 2 * direction, currentCol);
                // Kiểm tra xem ô 2 bước phía trước có trống không (ô 1 bước đã được kiểm tra ở trên)
                if (isValid(twoStepsForward) && board.getPieceAt(twoStepsForward) == null) {
                    moves.add(twoStepsForward);
                }
            }
        }

        // 3. KIỂM TRA 2 NƯỚC ĂN CHÉO
        int[] captureCols = {currentCol - 1, currentCol + 1}; // Cột bên trái và bên phải
        for (int captureCol : captureCols) {
            Position diagonalPos = new Position(currentRow + direction, captureCol);
            
            // Kiểm tra xem ô chéo có nằm trong bàn cờ không
            if (isValid(diagonalPos)) {
                Piece pieceToCapture = board.getPieceAt(diagonalPos);
                // Nếu ở đó có quân cờ và quân cờ đó là của đối phương
                if (pieceToCapture != null && pieceToCapture.getColor() != this.getColor()) {
                    moves.add(diagonalPos);
                }
            }
        }

        return moves;
    }

    /**
     * Một phương thức pomocniczy để kiểm tra xem một vị trí có hợp lệ trên bàn cờ không.
     * @param pos Vị trí cần kiểm tra.
     * @return true nếu vị trí nằm trong bàn cờ (0-7), ngược lại là false.
     */
    private boolean isValid(Position pos) {
        return pos.row() >= 0 && pos.row() < 8 && pos.col() >= 0 && pos.col() < 8;
    }
}