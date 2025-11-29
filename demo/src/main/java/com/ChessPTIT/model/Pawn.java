package com.ChessPTIT.model;

import com.ChessPTIT.service.GameService;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color);
    }

    @Override
    public List<Position> getValidMoves(Board board, Position currentPosition, GameService gameService) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        // Xác định hướng đi của Tốt
        int direction = (this.getColor() == PieceColor.WHITE) ? -1 : 1;

        // 1. KIỂM TRA NƯỚC ĐI THẲNG 1 Ô 
        Position oneStepForward = new Position(currentRow + direction, currentCol);
        if (isValid(oneStepForward) && board.getPieceAt(oneStepForward) == null) {
            moves.add(oneStepForward);

            // 2. KIỂM TRA NƯỚC ĐI THẲNG 2 Ô 
            boolean isAtStartingRow = (this.getColor() == PieceColor.WHITE && currentRow == 6) ||
                    (this.getColor() == PieceColor.BLACK && currentRow == 1);
            if (isAtStartingRow) {
                Position twoStepsForward = new Position(currentRow + 2 * direction, currentCol);
                if (isValid(twoStepsForward) && board.getPieceAt(twoStepsForward) == null) {
                    moves.add(twoStepsForward);
                }
            }
        }

        // 3. KIỂM TRA 2 NƯỚC ĂN CHÉO 
        int[] captureCols = { currentCol - 1, currentCol + 1 };
        for (int captureCol : captureCols) {
            Position diagonalPos = new Position(currentRow + direction, captureCol);

            if (isValid(diagonalPos)) {
                Piece pieceToCapture = board.getPieceAt(diagonalPos);
                if (pieceToCapture != null && pieceToCapture.getColor() != this.getColor()) {
                    moves.add(diagonalPos);
                }
            }
        }

        // LOGIC BẮT TỐT QUA ĐƯỜNG
        Position enPassantTarget = gameService.getEnPassantTargetSquare();
        if (enPassantTarget != null) {
            // Kiểm tra xem một trong hai ô ăn chéo có phải là ô en passant không
            if (enPassantTarget.row() == currentRow + direction &&
                    (enPassantTarget.col() == currentCol - 1 || enPassantTarget.col() == currentCol + 1)) {
                moves.add(enPassantTarget);
            }
        }

        return moves;
    }

    private boolean isValid(Position pos) {
        return pos.row() >= 0 && pos.row() < 8 && pos.col() >= 0 && pos.col() < 8;
    }
}