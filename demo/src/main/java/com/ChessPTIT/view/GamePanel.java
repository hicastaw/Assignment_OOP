package com.ChessPTIT.view;

import com.ChessPTIT.model.Board;
import com.ChessPTIT.model.Piece;
import com.ChessPTIT.model.PieceColor; // <-- ĐÃ THAY ĐỔI
import com.ChessPTIT.model.Position;
import com.ChessPTIT.service.GameService;

import javax.swing.*;

// <-- THÊM IMPORT NÀY
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final JButton[][] boardSquares = new JButton[8][8];
    private final GameService gameService;
    private final PanelSwitcher panelSwitcher;
    private boolean isGameOver = false;

    private JPanel boardPanel;
    private JLabel statusLabel;
    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;

    private Position selectedPosition = null;
    private Piece selectedPiece = null;
    private List<Position> validMoves = new ArrayList<>();

    public GamePanel(PanelSwitcher switcher, GameService service) {
        this.panelSwitcher = switcher;
        this.gameService = service;
        setLayout(new BorderLayout(10, 10));
        initializeUI();
    }

    // Trong GamePanel.java
    private void initializeUI() {
        // 1. Status Panel (giữ nguyên)
        statusLabel = new JLabel("Lượt của quân Trắng", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(statusLabel, BorderLayout.NORTH);

        // 2. Board Panel và Wrapper Panel (giữ nguyên)
        boardPanel = new JPanel(new GridLayout(8, 8));
        Dimension boardSize = new Dimension(640, 640);
        boardPanel.setPreferredSize(boardSize);
        // ... code tạo 64 nút bấm như cũ ...
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = new JButton();
                if ((row + col) % 2 == 0) {
                    square.setBackground(new Color(232, 235, 239));
                } else {
                    square.setBackground(new Color(125, 135, 150));
                }
                square.setBorder(BorderFactory.createEmptyBorder());
                square.setFocusPainted(false);
                final int r = row;
                final int c = col;
                square.addActionListener(e -> onSquareClicked(r, c));
                boardSquares[row][col] = square;
                boardPanel.add(square);
            }
        }
        JPanel centerWrapperPanel = new JPanel(new GridBagLayout());
        centerWrapperPanel.add(boardPanel);
        add(centerWrapperPanel, BorderLayout.CENTER);

        // 3. Cập nhật Panel cho các quân cờ bị ăn
        Dimension capturedPanelSize = new Dimension(80, 0); // Đủ rộng cho 2 quân cờ mỗi hàng

        whiteCapturedPanel = new JPanel(new GridLayout(8, 2, 5, 5)); // Lưới 8 hàng, 2 cột
        whiteCapturedPanel.setPreferredSize(capturedPanelSize);
        whiteCapturedPanel.setBorder(BorderFactory.createTitledBorder("Quân Đen bị ăn"));
        add(whiteCapturedPanel, BorderLayout.WEST);

        blackCapturedPanel = new JPanel(new GridLayout(8, 2, 5, 5)); // Lưới 8 hàng, 2 cột
        blackCapturedPanel.setPreferredSize(capturedPanelSize);
        blackCapturedPanel.setBorder(BorderFactory.createTitledBorder("Quân Trắng bị ăn"));
        add(blackCapturedPanel, BorderLayout.EAST);
    }

    private void onSquareClicked(int row, int col) {
        if (isGameOver) {
            return;
        }
        Position clickedPos = new Position(row, col);

        if (selectedPiece == null) {
            Piece piece = gameService.getBoard().getPieceAt(clickedPos);
            // SỬ DỤNG PieceColor
            if (piece != null && piece.getColor() == gameService.getCurrentPlayer()) {
                selectedPiece = piece;
                selectedPosition = clickedPos;
                validMoves = selectedPiece.getValidMoves(gameService.getBoard(), selectedPosition);
                highlightSquares(true);
            }
        } else {
            if (validMoves.contains(clickedPos)) {
                boolean moveSuccess = gameService.handleMove(selectedPosition, clickedPos);
                if (moveSuccess) {
                    redrawBoard();
                    updateStatus();
                    updateCapturedPanels();
                }
            }
            highlightSquares(false);
            selectedPiece = null;
            selectedPosition = null;
            validMoves.clear();
        }
    }

    private void highlightSquares(boolean highlight) {
        if (selectedPosition != null) {
            JButton selectedSquare = boardSquares[selectedPosition.row()][selectedPosition.col()];
            if (highlight) {
                // SỬ DỤNG Color từ java.awt
                selectedSquare.setBackground(new Color(186, 202, 68));
            } else {
                if ((selectedPosition.row() + selectedPosition.col()) % 2 == 0) {
                    selectedSquare.setBackground(new Color(232, 235, 239));
                } else {
                    selectedSquare.setBackground(new Color(125, 135, 150));
                }
            }
        }
    }

    public void updateStatus() {
        GameService.GameState state = gameService.getGameState();
        String statusText = "";
        statusLabel.setForeground(Color.BLACK); // Reset màu chữ về mặc định

        switch (state) {
            case IN_PROGRESS:
                // SỬ DỤNG PieceColor
                statusText = "Lượt của quân " + (gameService.getCurrentPlayer() == PieceColor.WHITE ? "Trắng" : "Đen");
                break;
            case CHECK:
                statusText = "CHIẾU! Lượt của quân "
                        + (gameService.getCurrentPlayer() == PieceColor.WHITE ? "Trắng" : "Đen");
                statusLabel.setForeground(Color.ORANGE);
                break;
            case CHECKMATE:
                String winner = (gameService.getCurrentPlayer() == PieceColor.WHITE) ? "Đen" : "Trắng";
                statusText = "CHIẾU BÍ! Quân " + winner + " thắng!";
                statusLabel.setForeground(Color.RED);
                isGameOver = true;
                break;
            case STALEMATE:
                statusText = "HÒA CỜ!";
                statusLabel.setForeground(Color.BLUE);
                isGameOver = true;
                break;
        }
        statusLabel.setText(statusText);
        if (isGameOver) {
            JOptionPane.showMessageDialog(this, statusText, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void updateCapturedPanels() {
        whiteCapturedPanel.removeAll();
        blackCapturedPanel.removeAll();
        for (Piece piece : gameService.getCapturedPieces()) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + getPieceImageName(piece)));
            Image image = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            JLabel pieceLabel = new JLabel(new ImageIcon(image));
            // SỬ DỤNG PieceColor
            if (piece.getColor() == PieceColor.BLACK) {
                whiteCapturedPanel.add(pieceLabel);
            } else {
                blackCapturedPanel.add(pieceLabel);
            }
        }
        whiteCapturedPanel.revalidate();
        whiteCapturedPanel.repaint();
        blackCapturedPanel.revalidate();
        blackCapturedPanel.repaint();
    }

    public void redrawBoard() {
        Board board = gameService.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                JButton square = boardSquares[row][col];
                if (piece != null) {
                    try {
                        ImageIcon icon = new ImageIcon(getClass().getResource("/" + getPieceImageName(piece)));
                        Image image = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        square.setIcon(new ImageIcon(image));
                    } catch (Exception e) {
                        square.setIcon(null);
                        System.err.println("Could not find image: " + getPieceImageName(piece));
                    }
                } else {
                    square.setIcon(null);
                }
            }
        }
    }

    private String getPieceImageName(Piece piece) {
        // SỬ DỤNG PieceColor
        return piece.getColor().name() + "_" + piece.getClass().getSimpleName().toUpperCase() + ".png";
    }
}