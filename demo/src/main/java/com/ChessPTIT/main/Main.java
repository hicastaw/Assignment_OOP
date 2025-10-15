// Sửa lại package cho đúng với cấu trúc project của bạn
package com.ChessPTIT.main;

// Sửa lại các lệnh import cho đúng
import com.ChessPTIT.db.DatabaseInitializer;
import com.ChessPTIT.view.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo database và bảng
        DatabaseInitializer.initialize();

        // 2. Chạy giao diện trên luồng đặc biệt của Swing (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}