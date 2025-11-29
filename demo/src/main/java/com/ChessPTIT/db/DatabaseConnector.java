package com.ChessPTIT.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnector {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String APP_FOLDER_PATH = USER_HOME + File.separator + ".ChessPTIT";
    private static final String DB_PATH = APP_FOLDER_PATH + File.separator + "chess_history.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    static {
        File appFolder = new File(APP_FOLDER_PATH);
        if (!appFolder.exists()) {
            boolean created = appFolder.mkdirs();
            System.out.println("Attempting to create directory: " + APP_FOLDER_PATH + " - Success: " + created);
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Attempting to connect to: " + URL);
        try {
            // Cố gắng tải driver một cách tường minh
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Connection successful!");
            return conn;

        } catch (SQLException | ClassNotFoundException e) {
            // Nếu có lỗi, in ra và hiển thị một hộp thoại lỗi
            System.err.println("DATABASE CONNECTION FAILED!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Không thể kết nối đến database tại:\n" + DB_PATH + "\n\nLỗi: " + e.getMessage(),
                    "Lỗi Kết nối Database",
                    JOptionPane.ERROR_MESSAGE);
            // Ném lại lỗi để các lớp gọi nó biết rằng đã có sự cố
            throw new SQLException("Failed to connect to the database.", e);
        }
    }
}