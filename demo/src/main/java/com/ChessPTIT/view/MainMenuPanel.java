package com.ChessPTIT.view;

import com.ChessPTIT.service.GameService;
import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    private PanelSwitcher panelSwitcher;
    private GameService gameService;

    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 60);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Color TITLE_COLOR = new Color(220, 50, 50);

    public MainMenuPanel(PanelSwitcher switcher, GameService service) {
        this.panelSwitcher = switcher;
        this.gameService = service;

        setLayout(new GridBagLayout());
        setBackground(BG_COLOR);

        initializeUI();
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Chess PTIT", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TITLE_COLOR);

        gbc.gridy = 0;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        JButton newGameButton = createStyledButton("Trò chơi mới");
        JButton historyButton = createStyledButton("Lịch sử");
        JButton exitButton = createStyledButton("Thoát");

        newGameButton.addActionListener(e -> {
            String player1Name = JOptionPane.showInputDialog(this, "Nhập tên người chơi 1 (Quân Trắng):",
                    "Bắt đầu ván đấu", JOptionPane.PLAIN_MESSAGE);
            if (player1Name == null || player1Name.trim().isEmpty())
                return;

            String player2Name = JOptionPane.showInputDialog(this, "Nhập tên người chơi 2 (Quân Đen):",
                    "Bắt đầu ván đấu", JOptionPane.PLAIN_MESSAGE);
            if (player2Name == null || player2Name.trim().isEmpty())
                return;

            gameService.startNewGame(player1Name, player2Name);
            panelSwitcher.switchToPanel(MainFrame.GAME_PANEL);
        });
        historyButton.addActionListener(e -> panelSwitcher.switchToPanel(MainFrame.HISTORY_PANEL));
        exitButton.addActionListener(e -> System.exit(0));
        buttonsPanel.add(newGameButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(historyButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(exitButton);
        gbc.gridy = 1;
        gbc.weighty = 0.7;
        gbc.anchor = GridBagConstraints.NORTH;
        add(buttonsPanel, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(200, 50);
        button.setMaximumSize(buttonSize);
        button.setPreferredSize(buttonSize);

        return button;
    }
}