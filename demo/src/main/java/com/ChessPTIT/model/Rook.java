package com.ChessPTIT.model;

import com.ChessPTIT.service.GameService; 
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    private boolean hasMoved = false;

    public Rook(PieceColor color) {
        super(color);
    }

    @Override
    public List<Position> getValidMoves(Board board, Position currentPosition, GameService gameService) {
        List<Position> moves = new ArrayList<>();
        int currentRow = currentPosition.row();
        int currentCol = currentPosition.col();

        // Mảng hướng đi: lên, xuống, trái, phải
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        for (int[] dir : directions) {
            int nextRow = currentRow + dir[0];
            int nextCol = currentCol + dir[1];

            // Tiếp tục đi theo một hướng cho đến khi gặp biên hoặc một quân cờ khác
            while (nextRow >= 0 && nextRow < 8 && nextCol >= 0 && nextCol < 8) {
                Position nextPos = new Position(nextRow, nextCol);
                Piece pieceAtNextPos = board.getPieceAt(nextPos);

                if (pieceAtNextPos == null) {
                    // Nếu ô trống, đây là một nước đi hợp lệ
                    moves.add(nextPos);
                } else {
                    // Nếu có quân cờ ở ô tiếp theo
                    if (pieceAtNextPos.getColor() != this.color) {
                        // Nếu là quân địch, có thể ăn. Đây là nước đi hợp lệ cuối cùng theo hướng này.
                        moves.add(nextPos);
                    }
                    // Dù là quân địch hay quân ta, cũng phải dừng lại vì không thể đi xuyên qua.
                    break;
                }

                // Di chuyển đến ô tiếp theo cùng hướng
                nextRow += dir[0];
                nextCol += dir[1];
            }
        }
        return moves;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}