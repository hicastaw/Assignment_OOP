package com.ChessPTIT.view;

import com.ChessPTIT.service.GameService; // <-- Thêm import này
import javax.swing.*;

public class MainMenuPanel extends JPanel {

    private PanelSwitcher panelSwitcher;
    private GameService gameService;

    public MainMenuPanel(PanelSwitcher switcher, GameService service) {
        this.panelSwitcher = switcher;
        this.gameService = service;

        JButton newGameButton = new JButton("Trò chơi mới");
        JButton historyButton = new JButton("Lịch sử");
        JButton exitButton = new JButton("Thoát");

        // --- XỬ LÝ SỰ KIỆN CHO NÚT "TRÒ CHƠI MỚI" ---
        newGameButton.addActionListener(e -> {
            // 1. Hiện hộp thoại để nhập tên người chơi 1 (Trắng)
            String player1Name = JOptionPane.showInputDialog(
                    this, // component cha
                    "Nhập tên người chơi 1 (Quân Trắng):", // thông điệp
                    "Bắt đầu ván đấu", // tiêu đề
                    JOptionPane.PLAIN_MESSAGE);
            // Nếu người dùng nhấn Cancel hoặc đóng hộp thoại, player1Name sẽ là null
            if (player1Name == null || player1Name.trim().isEmpty()) {
                return; // Không làm gì cả
            }

            // 2. Hiện hộp thoại để nhập tên người chơi 2 (Đen)
            String player2Name = JOptionPane.showInputDialog(
                    this,
                    "Nhập tên người chơi 2 (Quân Đen):",
                    "Bắt đầu ván đấu",
                    JOptionPane.PLAIN_MESSAGE);
            if (player2Name == null || player2Name.trim().isEmpty()) {
                return; // Không làm gì cả
            }

            // 3. Gọi GameService để bắt đầu logic game
            gameService.startNewGame(player1Name, player2Name);

            // 4. Yêu cầu MainFrame chuyển sang màn hình chơi game
            panelSwitcher.switchToPanel(MainFrame.GAME_PANEL);

            // TODO: Yêu cầu GamePanel cập nhật lại giao diện bàn cờ
        });

        historyButton.addActionListener(e -> panelSwitcher.switchToPanel(MainFrame.HISTORY_PANEL));
        exitButton.addActionListener(e -> System.exit(0));

        add(newGameButton);
        add(historyButton);
        add(exitButton);
    }
}