package com.ChessPTIT.service;

import com.ChessPTIT.db.MatchDAO;
import com.ChessPTIT.db.MoveDAO;
import com.ChessPTIT.model.*;
import java.util.ArrayList;
import java.util.List;

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
    private Position enPassantTargetSquare;

    public enum GameState {
        IN_PROGRESS, CHECK, CHECKMATE, STALEMATE, PAWN_PROMOTION
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
        this.enPassantTargetSquare = null;
        this.currentMatchId = matchDAO.createNewMatch(player1Name, player2Name);
    }

    public boolean handleMove(Position from, Position to) {
        Piece pieceToMove = board.getPieceAt(from);

        if (pieceToMove == null || pieceToMove.getColor() != currentPlayer) {
            return false;
        }

        // SỬA LỖI: Lấy danh sách nước đi hợp lệ TRƯỚC KHI reset enPassantTargetSquare
        List<Position> validMoves = pieceToMove.getValidMoves(board, from, this);
        if (!validMoves.contains(to)) {
            return false;
        }

        // SỬA LỖI: Lưu lại giá trị en passant của lượt này trước khi nó bị thay đổi
        Position previousEnPassantTarget = this.enPassantTargetSquare;

        // SỬA LỖI: Reset ô en passant ngay sau khi đã kiểm tra nước đi hợp lệ
        this.enPassantTargetSquare = null;

        if (moveResultsInCheck(from, to, currentPlayer)) {
            return false;
        }

        boolean isCapture = board.getPieceAt(to) != null
                || (pieceToMove instanceof Pawn && to.equals(previousEnPassantTarget));

        // Xử lý di chuyển
        if (pieceToMove instanceof King && isCastlingMove(from, to)) {
            if (canCastle((King) pieceToMove, from, to)) {
                performCastling(from, to);
            } else {
                return false;
            }
        } else if (pieceToMove instanceof Pawn && to.equals(previousEnPassantTarget)) {
            performEnPassant(from, to);
        } else {
            performNormalMove(from, to);
        }

        // Tạo ký hiệu nước đi cơ bản
        String notation = convertToNotation(pieceToMove, from, to, isCapture);

        // Thiết lập ô enPassant cho lượt tiếp theo nếu Tốt đi 2 ô
        if (pieceToMove instanceof Pawn && Math.abs(from.row() - to.row()) == 2) {
            int direction = (pieceToMove.getColor() == PieceColor.WHITE) ? -1 : 1;
            this.enPassantTargetSquare = new Position(from.row() + direction, from.col());
        }

        // Xử lý Phong cấp
        if (pieceToMove instanceof Pawn) {
            boolean isPromotion = (pieceToMove.getColor() == PieceColor.WHITE && to.row() == 0) ||
                    (pieceToMove.getColor() == PieceColor.BLACK && to.row() == 7);
            if (isPromotion) {
                this.gameState = GameState.PAWN_PROMOTION;
                moveHistory.add(notation);
                return true;
            }
        }

        // Chuyển lượt và cập nhật trạng thái game
        switchPlayer();

        // Thêm ký hiệu chiếu/chiếu bí vào nước đi
        if (gameState == GameState.CHECKMATE) {
            notation += "#";
        } else if (gameState == GameState.CHECK) {
            notation += "+";
        }

        moveHistory.add(notation);

        return true;
    }

    // ... các phương thức còn lại giữ nguyên như phiên bản hoàn chỉnh trước đó ...

    public void promotePawn(Position position, String choice) {
        PieceColor promotionColor = board.getPieceAt(position).getColor();
        Piece newPiece;
        switch (choice.toUpperCase()) {
            case "QUEEN":
                newPiece = new Queen(promotionColor);
                break;
            case "ROOK":
                newPiece = new Rook(promotionColor);
                break;
            case "BISHOP":
                newPiece = new Bishop(promotionColor);
                break;
            case "KNIGHT":
                newPiece = new Knight(promotionColor);
                break;
            default:
                newPiece = new Queen(promotionColor);
        }
        board.setPieceAt(position, newPiece);
        if (!moveHistory.isEmpty()) {
            int lastMoveIndex = moveHistory.size() - 1;
            String lastMove = moveHistory.get(lastMoveIndex);
            moveHistory.set(lastMoveIndex, lastMove + "=" + choice.toUpperCase().charAt(0));
        }
        switchPlayer();
        if (!moveHistory.isEmpty()) {
            int lastMoveIndex = moveHistory.size() - 1;
            String lastMove = moveHistory.get(lastMoveIndex);
            if (gameState == GameState.CHECKMATE) {
                moveHistory.set(lastMoveIndex, lastMove + "#");
            } else if (gameState == GameState.CHECK) {
                moveHistory.set(lastMoveIndex, lastMove + "+");
            }
        }
    }

    private void performNormalMove(Position from, Position to) {
        Piece pieceToMove = board.getPieceAt(from);
        Piece capturedPiece = board.getPieceAt(to);
        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
        }
        board.movePiece(from, to);
        updateHasMovedFlag(pieceToMove);
    }

    private void performCastling(Position kingFrom, Position kingTo) {
        board.movePiece(kingFrom, kingTo);
        Position rookFrom, rookTo;
        if (kingTo.col() == 6) {
            rookFrom = new Position(kingFrom.row(), 7);
            rookTo = new Position(kingFrom.row(), 5);
        } else {
            rookFrom = new Position(kingFrom.row(), 0);
            rookTo = new Position(kingFrom.row(), 3);
        }
        Piece rook = board.getPieceAt(rookFrom);
        board.movePiece(rookFrom, rookTo);
        updateHasMovedFlag(board.getPieceAt(kingTo));
        updateHasMovedFlag(rook);
    }

    private void performEnPassant(Position from, Position to) {
        Piece pieceToMove = board.getPieceAt(from);
        int capturedPawnRow = (pieceToMove.getColor() == PieceColor.WHITE) ? to.row() + 1 : to.row() - 1;
        Position capturedPawnPos = new Position(capturedPawnRow, to.col());
        Piece capturedPiece = board.getPieceAt(capturedPawnPos);
        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
        }
        board.movePiece(from, to);
        board.setPieceAt(capturedPawnPos, null);
    }

    private void updateHasMovedFlag(Piece piece) {
        if (piece instanceof Rook)
            ((Rook) piece).setHasMoved(true);
        if (piece instanceof King)
            ((King) piece).setHasMoved(true);
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        updateGameState();
    }

    private void updateGameState() {
        boolean isInCheck = isKingInCheck(currentPlayer);
        List<Position> allLegalMoves = getAllLegalMoves(currentPlayer);
        if (allLegalMoves.isEmpty()) {
            if (isInCheck) {
                this.gameState = GameState.CHECKMATE;
                String winner = (currentPlayer == PieceColor.WHITE) ? "Black" : "White";
                endGame(winner + " wins by Checkmate");
            } else {
                this.gameState = GameState.STALEMATE;
                endGame("Draw by Stalemate");
            }
        } else if (isInCheck) {
            this.gameState = GameState.CHECK;
        } else {
            this.gameState = GameState.IN_PROGRESS;
        }
    }

    private void endGame(String result) {
        if (currentMatchId > 0) {
            matchDAO.updateMatchResult(currentMatchId, result);
            moveDAO.saveMovesForMatch(currentMatchId, moveHistory);
        }
    }

    private String convertToNotation(Piece piece, Position from, Position to, boolean isCapture) {
        if (piece instanceof King && Math.abs(from.col() - to.col()) == 2) {
            return (to.col() == 6) ? "0-0" : "0-0-0";
        }
        StringBuilder notation = new StringBuilder();
        if (!(piece instanceof Pawn)) {
            String pieceName = piece.getClass().getSimpleName();
            notation.append(pieceName.equals("Knight") ? "N" : pieceName.charAt(0));
        } else if (isCapture) {
            notation.append((char) ('a' + from.col()));
        }
        if (isCapture) {
            notation.append("x");
        }
        notation.append((char) ('a' + to.col()));
        notation.append(8 - to.row());
        return notation.toString();
    }

    private boolean moveResultsInCheck(Position from, Position to, PieceColor playerColor) {
        Board tempBoard = board.deepClone();
        tempBoard.movePiece(from, to);
        return isKingInCheckOnBoard(tempBoard, playerColor);
    }

    private boolean isKingInCheckOnBoard(Board boardToCheck, PieceColor kingColor) {
        Position kingPosition = findKingPosition(boardToCheck, kingColor);
        if (kingPosition == null)
            return false;
        PieceColor opponentColor = (kingColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        List<Position> opponentMoves = getAllPossibleRawMovesOnBoard(boardToCheck, opponentColor);
        return opponentMoves.contains(kingPosition);
    }

    private boolean isCastlingMove(Position from, Position to) {
        return board.getPieceAt(from) instanceof King && Math.abs(from.col() - to.col()) == 2;
    }

    private boolean canCastle(King king, Position from, Position to) {
        if (isKingInCheck(king.getColor()))
            return false;
        int direction = to.col() > from.col() ? 1 : -1;
        Position passingSquare = new Position(from.row(), from.col() + direction);
        return !isSquareAttacked(passingSquare, king.getColor()) && !isSquareAttacked(to, king.getColor());
    }

    private boolean isSquareAttacked(Position square, PieceColor playerColor) {
        PieceColor opponentColor = (playerColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        List<Position> opponentMoves = getAllPossibleRawMovesOnBoard(board, opponentColor);
        return opponentMoves.contains(square);
    }

    public boolean isKingInCheck(PieceColor kingColor) {
        return isKingInCheckOnBoard(this.board, kingColor);
    }

    private List<Position> getAllLegalMoves(PieceColor playerColor) {
        List<Position> allLegalMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = board.getPieceAt(from);
                if (p != null && p.getColor() == playerColor) {
                    List<Position> movesForPiece = p.getValidMoves(board, from, this);
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

    private List<Position> getAllPossibleRawMovesOnBoard(Board board, PieceColor playerColor) {
        List<Position> allMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position pos = new Position(r, c);
                Piece p = board.getPieceAt(pos);
                if (p != null && p.getColor() == playerColor) {
                    allMoves.addAll(p.getValidMoves(board, pos, this));
                }
            }
        }
        return allMoves;
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

    /**
     * Lấy danh sách tất cả các nước đi hợp lệ thực sự cho một quân cờ tại một vị
     * trí.
     * Phương thức này sẽ lọc ra các nước đi tự làm Vua của mình bị chiếu.
     * 
     * @param from Vị trí của quân cờ cần kiểm tra.
     * @return Một danh sách các nước đi hợp lệ.
     */
    public List<Position> getLegalMovesForPiece(Position from) {
        List<Position> legalMoves = new ArrayList<>();
        Piece piece = board.getPieceAt(from);
        if (piece == null || piece.getColor() != currentPlayer) {
            return legalMoves; // Trả về danh sách rỗng nếu không có quân cờ hoặc không phải lượt
        }

        // Lấy danh sách các nước đi tiềm năng
        List<Position> potentialMoves = piece.getValidMoves(board, from, this);

        // Lọc ra những nước đi không làm Vua bị chiếu
        for (Position to : potentialMoves) {
            // Xử lý riêng cho nhập thành vì nó phức tạp hơn
            if (piece instanceof King && isCastlingMove(from, to)) {
                if (canCastle((King) piece, from, to)) {
                    legalMoves.add(to);
                }
            }
            // Xử lý các nước đi còn lại
            else if (!moveResultsInCheck(from, to, currentPlayer)) {
                legalMoves.add(to);
            }
        }

        return legalMoves;
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

    public Position getEnPassantTargetSquare() {
        return this.enPassantTargetSquare;
    }
}