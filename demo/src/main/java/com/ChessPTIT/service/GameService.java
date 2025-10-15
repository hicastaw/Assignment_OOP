package com.ChessPTIT.service;

import java.util.ArrayList;
import java.util.List;

import com.ChessPTIT.db.MatchDAO;
import com.ChessPTIT.db.MoveDAO;
// Import tất cả các lớp trong model
import com.ChessPTIT.model.Board;
import com.ChessPTIT.model.PieceColor;
import com.ChessPTIT.model.King;
import com.ChessPTIT.model.Piece;
import com.ChessPTIT.model.PieceColor;
import com.ChessPTIT.model.Player;
import com.ChessPTIT.model.Position;
import com.ChessPTIT.model.Rook;

// Lỗi 1: Đã xóa "import javax.swing.text.Position;" và đảm bảo import đúng lớp Position từ model

public class GameService {

    private Board board;
    private Player player1;
    private Player player2;
    private PieceColor currentPlayer;
    private List<Piece> capturedPieces;
    private List<String> moveHistory;
    private int currentMatchId;
    private MatchDAO matchDAO;
    private MoveDAO moveDAO;
    private GameState gameState;

    // Enum để quản lý các trạng thái của game
    public enum GameState {
        IN_PROGRESS,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    public GameService() {
        this.matchDAO = new MatchDAO();
        this.moveDAO = new MoveDAO();
        this.board = new Board();
        this.gameState = GameState.IN_PROGRESS;
    }

    public void startNewGame(String player1Name, String player2Name) {
        board.setupBoard();
        this.player1 = new Player(player1Name, PieceColor.WHITE);
        this.player2 = new Player(player2Name, PieceColor.BLACK);
        this.currentPlayer = PieceColor.WHITE;
        this.capturedPieces = new ArrayList<>();
        this.moveHistory = new ArrayList<>();
        this.gameState = GameState.IN_PROGRESS;

        // Bước cuối: Đã kích hoạt dòng code này
        this.currentMatchId = matchDAO.createNewMatch(player1Name, player2Name);
        System.out.println(
                "New game started (ID: " + this.currentMatchId + ") between " + player1Name + " and " + player2Name);
    }

    public boolean handleMove(Position from, Position to) {
        Piece pieceToMove = board.getPieceAt(from);

        if (pieceToMove == null || pieceToMove.getColor() != currentPlayer) {
            return false;
        }

        List<Position> validMoves = pieceToMove.getValidMoves(board, from);
        if (!validMoves.contains(to)) {
            return false;
        }

        if (moveResultsInCheck(from, to, currentPlayer)) {
            return false;
        }

        Piece capturedPiece = board.getPieceAt(to);
        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
        }

        board.movePiece(from, to);

        if (pieceToMove instanceof Rook)
            ((Rook) pieceToMove).setHasMoved(true);
        if (pieceToMove instanceof King)
            ((King) pieceToMove).setHasMoved(true);

        moveHistory.add(from.toString() + "-" + to.toString());

        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        updateGameState();

        return true;
    }

    private void updateGameState() {
        PieceColor opponentColor = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        boolean isInCheck = isKingInCheck(currentPlayer);

        List<Position> allPossibleMoves = getAllPossibleMoves(currentPlayer);

        if (allPossibleMoves.isEmpty()) {
            if (isInCheck) {
                this.gameState = GameState.CHECKMATE;
                String result = (opponentColor == PieceColor.WHITE ? "White" : "Black") + " wins by Checkmate";
                System.out.println("CHECKMATE! " + opponentColor + " wins!");
                endGame(result);
            } else {
                this.gameState = GameState.STALEMATE;
                System.out.println("STALEMATE! It's a draw.");
                endGame("Draw by Stalemate");
            }
        } else if (isInCheck) {
            this.gameState = GameState.CHECK;
            System.out.println(currentPlayer + " is in CHECK!");
        } else {
            this.gameState = GameState.IN_PROGRESS;
        }
    }

    // Lỗi 2: Đã thêm phương thức endGame bị thiếu
    /**
     * Xử lý việc kết thúc ván đấu và lưu kết quả vào database.
     * 
     * @param result Chuỗi mô tả kết quả cuối cùng của ván đấu.
     */
    private void endGame(String result) {
        System.out.println("Game Over. Result: " + result);
        if (currentMatchId > 0) { // Đảm bảo ID ván đấu hợp lệ
            // 1. Cập nhật kết quả cuối cùng trong bảng `matches`
            matchDAO.updateMatchResult(currentMatchId, result);
            // 2. Lưu lại toàn bộ các nước đi vào bảng `moves`
            moveDAO.saveMovesForMatch(currentMatchId, moveHistory);
            System.out.println("Match (ID: " + currentMatchId + ") and its moves have been saved to the database.");
        }
    }

    private boolean moveResultsInCheck(Position from, Position to, PieceColor playerColor) {
        Board tempBoard = board.deepClone();
        tempBoard.movePiece(from, to);
        return isKingInCheckOnBoard(tempBoard, playerColor);
    }

    private boolean isKingInCheckOnBoard(Board boardToCheck, PieceColor kingColor) {
        Position kingPosition = findKingPosition(boardToCheck, kingColor);
        if (kingPosition == null)
            return true;

        PieceColor opponentColor = (kingColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        List<Position> opponentMoves = getAllPossibleMovesOnBoard(boardToCheck, opponentColor);
        return opponentMoves.contains(kingPosition);
    }

    public Board getBoard() {
        return this.board;
    }

    public PieceColor getCurrentPlayer() {
        return this.currentPlayer;
    }

    public List<Piece> getCapturedPieces() {
        return this.capturedPieces;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public boolean isKingInCheck(PieceColor kingColor) {
        return isKingInCheckOnBoard(this.board, kingColor);
    }

    private Position findKingPosition(Board board, PieceColor kingColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position pos = new Position(r, c);
                Piece p = board.getPieceAt(pos);
                if (p instanceof King && p.getColor() == kingColor) {
                    return pos;
                }
            }
        }
        return null;
    }

    private List<Position> getAllPossibleMovesOnBoard(Board board, PieceColor playerColor) {
        List<Position> allMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position pos = new Position(r, c);
                Piece p = board.getPieceAt(pos);
                if (p != null && p.getColor() == playerColor) {
                    allMoves.addAll(p.getValidMoves(board, pos));
                }
            }
        }
        return allMoves;
    }

    private List<Position> getAllPossibleMoves(PieceColor playerColor) {
        List<Position> allLegalMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = board.getPieceAt(from);
                if (p != null && p.getColor() == playerColor) {
                    List<Position> movesForPiece = p.getValidMoves(board, from);
                    for (Position to : movesForPiece) {
                        if (!moveResultsInCheck(from, to, playerColor)) {
                            allLegalMoves.add(to);
                        }
                    }
                }
            }
        }
        return allLegalMoves;
    }
}