package com.ChessPTIT.view;

import com.ChessPTIT.service.GameService;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements PanelSwitcher {
    public static final String MENU_PANEL = "MENU";
    public static final String GAME_PANEL = "GAME";
    public static final String HISTORY_PANEL = "HISTORY";

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private GameService gameService;
    private GamePanel gamePanel; // Giữ lại tham chiếu để gọi phương thức
    private HistoryPanel historyPanel; // Giữ lại tham chiếu để gọi phương thức

    public MainFrame() {
        setTitle("Chess Game");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.gameService = new GameService();

        MainMenuPanel mainMenuPanel = new MainMenuPanel(this, this.gameService);
        this.gamePanel = new GamePanel(this, this.gameService);
        this.historyPanel = new HistoryPanel(this);

        mainPanel.add(mainMenuPanel, MENU_PANEL);
        mainPanel.add(gamePanel, GAME_PANEL);
        mainPanel.add(historyPanel, HISTORY_PANEL);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void switchToPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        if (panelName.equals(GAME_PANEL)) {
            // Khi vào game, reset lại trạng thái và vẽ lại bàn cờ
            // (Phần này có thể cần khi bạn thêm tính năng "Chơi lại")
            gamePanel.redrawBoard();
            gamePanel.updateStatus();
        }

        // THAY ĐỔI 2: Khi vào màn hình lịch sử, ra lệnh cho nó tải dữ liệu mới
        if (panelName.equals(HISTORY_PANEL)) {
            historyPanel.loadAndDisplayHistory();
        }
    }
}