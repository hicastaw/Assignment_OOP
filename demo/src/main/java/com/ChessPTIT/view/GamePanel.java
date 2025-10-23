package com.ChessPTIT.view;

import com.ChessPTIT.model.Board;
import com.ChessPTIT.model.Piece;
import com.ChessPTIT.model.PieceColor;
import com.ChessPTIT.model.Position;
import com.ChessPTIT.service.GameService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final JButton[][] boardSquares = new JButton[8][8];
    private final GameService gameService;
    private final PanelSwitcher panelSwitcher;
    private boolean isGameOver = false;

    // --- CÁC THÀNH PHẦN GIAO DIỆN ---
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;
    private JButton backToMenuButton;

    // --- CÁC THUỘC TÍNH QUẢN LÝ LỰA CHỌN ---
    private Position selectedPosition = null;
    private Piece selectedPiece = null;
    private List<Position> validMoves = new ArrayList<>();

    public GamePanel(PanelSwitcher switcher, GameService service) {
        this.panelSwitcher = switcher;
        this.gameService = service;
        setLayout(new BorderLayout(10, 10));
        initializeUI();
    }

    private void initializeUI() {
        // 1. Status Panel (ở trên)
        statusLabel = new JLabel("Lượt của quân Trắng", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(statusLabel, BorderLayout.NORTH);

        // 2. Board Panel (ở giữa, kích thước cố định)
        boardPanel = new JPanel(new GridLayout(8, 8));
        Dimension boardSize = new Dimension(640, 640);
        boardPanel.setPreferredSize(boardSize);
        boardPanel.setMinimumSize(boardSize);
        boardPanel.setMaximumSize(boardSize);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = new JButton();
                if ((row + col) % 2 == 0) {
                    square.setBackground(new Color(232, 235, 239)); // Màu sáng
                } else {
                    square.setBackground(new Color(125, 135, 150)); // Màu tối
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

        // Panel bọc để căn giữa bàn cờ
        JPanel centerWrapperPanel = new JPanel(new GridBagLayout());
        centerWrapperPanel.add(boardPanel);
        add(centerWrapperPanel, BorderLayout.CENTER);

        // 3. Captured Panels (hai bên)
        Dimension capturedPanelSize = new Dimension(100, 0);
        whiteCapturedPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        whiteCapturedPanel.setPreferredSize(capturedPanelSize);
        whiteCapturedPanel.setBorder(BorderFactory.createTitledBorder("Quân Đen bị ăn"));
        add(whiteCapturedPanel, BorderLayout.WEST);

        blackCapturedPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        blackCapturedPanel.setPreferredSize(capturedPanelSize);
        blackCapturedPanel.setBorder(BorderFactory.createTitledBorder("Quân Trắng bị ăn"));
        add(blackCapturedPanel, BorderLayout.EAST);

        // 4. Panel chứa nút "Quay lại Menu" (ở dưới)
        JPanel southPanel = new JPanel();
        backToMenuButton = new JButton("Quay lại Menu");
        backToMenuButton.setFont(new Font("Arial", Font.BOLD, 16));
        backToMenuButton.setVisible(false); // Ban đầu ẩn
        backToMenuButton.addActionListener(e -> {
            resetGamePanel();
            panelSwitcher.switchToPanel(MainFrame.MENU_PANEL);
        });
        southPanel.add(backToMenuButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void onSquareClicked(int row, int col) {
        if (isGameOver) {
            return;
        }
        Position clickedPos = new Position(row, col);

        if (selectedPiece == null) {
            // Lần click đầu tiên: CHỌN QUÂN CỜ
            Piece piece = gameService.getBoard().getPieceAt(clickedPos);
            if (piece != null && piece.getColor() == gameService.getCurrentPlayer()) {
                selectedPiece = piece;
                selectedPosition = clickedPos;
                validMoves = gameService.getLegalMovesForPiece(selectedPosition);
                highlightSquares(true); // Tô sáng ô chọn và các nước đi
            }
        } else {
            // Lần click thứ hai: DI CHUYỂN
            highlightSquares(false); // Luôn xóa highlight cũ

            if (validMoves.contains(clickedPos)) {
                boolean moveSuccess = gameService.handleMove(selectedPosition, clickedPos);
                if (moveSuccess) {
                    redrawBoard();
                    updateCapturedPanels();

                    if (gameService.getGameState() == GameService.GameState.PAWN_PROMOTION) {
                        handlePawnPromotion(clickedPos);
                    } else {
                        updateStatus();
                    }
                }
            }

            // Reset lại lựa chọn
            selectedPiece = null;
            selectedPosition = null;
            validMoves.clear();
        }
    }

    /**
     * Tô sáng ô được chọn VÀ hiển thị các nước đi hợp lệ.
     * 
     * @param highlight true để hiển thị, false để xóa các highlight.
     */
    private void highlightSquares(boolean highlight) {
        // Bỏ tô sáng các nước đi cũ trước khi làm mới
        for (Position pos : validMoves) {
            JButton square = boardSquares[pos.row()][pos.col()];
            // Xóa các chấm tròn gợi ý (nếu có)
            square.removeAll();
            // Trả lại viền mặc định
            square.setBorder(BorderFactory.createEmptyBorder());
            square.revalidate();
            square.repaint();
        }

        // Trả lại màu nền ban đầu cho ô đã chọn trước đó
        if (selectedPosition != null) {
            JButton previousSelectedSquare = boardSquares[selectedPosition.row()][selectedPosition.col()];
            if ((selectedPosition.row() + selectedPosition.col()) % 2 == 0) {
                previousSelectedSquare.setBackground(new Color(232, 235, 239)); // Màu sáng
            } else {
                previousSelectedSquare.setBackground(new Color(125, 135, 150)); // Màu tối
            }
        }

        // Nếu 'highlight' là true, tiến hành tô sáng các lựa chọn mới
        if (highlight) {
            // 1. Tô sáng ô đang được chọn
            if (selectedPosition != null) {
                JButton selectedSquare = boardSquares[selectedPosition.row()][selectedPosition.col()];
                selectedSquare.setBackground(new Color(186, 202, 68)); // Màu vàng
            }

            // 2. Hiển thị gợi ý cho các nước đi hợp lệ
            for (Position pos : validMoves) {
                JButton square = boardSquares[pos.row()][pos.col()];

                // Nếu ô đó có quân cờ (tức là nước ăn quân), ta vẽ viền tròn
                if (gameService.getBoard().getPieceAt(pos) != null) {
                    square.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0, 150), 4));
                } else { // Nếu ô trống, vẽ một chấm tròn ở giữa
                    square.setLayout(new BorderLayout());
                    JLabel hint = new JLabel("●");
                    hint.setHorizontalAlignment(SwingConstants.CENTER);
                    hint.setForeground(new Color(0, 0, 0, 80)); // Màu đen trong suốt
                    hint.setFont(new Font("Arial", Font.BOLD, 50));
                    square.add(hint);
                }
            }
        }
    }

    public void updateStatus() {
        GameService.GameState state = gameService.getGameState();
        String statusText = "";
        statusLabel.setForeground(Color.BLACK); // Reset màu chữ

        switch (state) {
            case IN_PROGRESS:
                statusText = "Lượt của quân " + (gameService.getCurrentPlayer() == PieceColor.WHITE ? "Trắng" : "Đen");
                break;
            case CHECK:
                statusText = "CHIẾU! Lượt của quân "
                        + (gameService.getCurrentPlayer() == PieceColor.WHITE ? "Trắng" : "Đen");
                statusLabel.setForeground(Color.ORANGE.darker());
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
            case PAWN_PROMOTION:
                statusText = "Phong cấp Tốt! Hãy chọn một quân cờ.";
                statusLabel.setForeground(new Color(0, 100, 0)); // Màu xanh đậm
                break;
        }
        statusLabel.setText(statusText);

        if (isGameOver) {
            backToMenuButton.setVisible(true);
            JOptionPane.showMessageDialog(this, statusText, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handlePawnPromotion(Position promotionPosition) {
        Object[] options = { "Queen", "Rook", "Bishop", "Knight" };
        String choice = (String) JOptionPane.showInputDialog(
                this, "Chọn một quân cờ để phong cấp:", "Phong cấp Tốt",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == null) { // Nếu người dùng nhấn Cancel
            choice = "Queen"; // Mặc định phong cấp Hậu
        }

        gameService.promotePawn(promotionPosition, choice);
        redrawBoard();
        updateStatus();
    }

    public void resetGamePanel() {
        isGameOver = false;
        backToMenuButton.setVisible(false);
        statusLabel.setForeground(Color.BLACK);
        whiteCapturedPanel.removeAll();
        blackCapturedPanel.removeAll();
        whiteCapturedPanel.revalidate();
        whiteCapturedPanel.repaint();
        blackCapturedPanel.revalidate();
        blackCapturedPanel.repaint();
    }

    public void updateCapturedPanels() {
        whiteCapturedPanel.removeAll();
        blackCapturedPanel.removeAll();
        for (Piece piece : gameService.getCapturedPieces()) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + getPieceImageName(piece)));
            Image image = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            JLabel pieceLabel = new JLabel(new ImageIcon(image));
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
        // Lấy tên lớp đơn giản và chuyển thành chữ hoa, ví dụ: "PAWN", "ROOK"
        String pieceType = piece.getClass().getSimpleName().toUpperCase();
        // Lấy tên màu, ví dụ: "WHITE", "BLACK"
        String pieceColor = piece.getColor().name();
        return pieceColor + "_" + pieceType + ".png";
    }
}