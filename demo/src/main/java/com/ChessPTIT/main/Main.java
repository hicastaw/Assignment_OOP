package com.ChessPTIT.main;

import com.ChessPTIT.db.DatabaseInitializer;
import com.ChessPTIT.view.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        // Chạy giao diện trên luồng đặc biệt của Swing (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}