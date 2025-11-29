package com.ChessPTIT.view;

import com.ChessPTIT.db.MatchDAO;
import com.ChessPTIT.db.MoveDAO;
import com.ChessPTIT.model.Match;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;

public class HistoryPanel extends JPanel {
    private final MatchDAO matchDAO;
    private final MoveDAO moveDAO;
    private JList<Match> matchesList;
    private JTextArea movesTextArea;
    private DefaultListModel<Match> listModel;

    public HistoryPanel(PanelSwitcher switcher) {
        this.matchDAO = new MatchDAO();
        this.moveDAO = new MoveDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Thêm khoảng đệm

        // Nút "Quay lại Menu"
        JButton backButton = new JButton("Quay lại Menu");
        backButton.addActionListener(e -> switcher.switchToPanel(MainFrame.MENU_PANEL));

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(backButton);
        add(southPanel, BorderLayout.SOUTH);

        // -- Panel chính được chia đôi --
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        add(splitPane, BorderLayout.CENTER);

        // 1. Panel bên trái: Hiển thị danh sách các ván đấu
        listModel = new DefaultListModel<>();
        matchesList = new JList<>(listModel);
        matchesList.setCellRenderer(new MatchListRenderer()); // Dùng renderer để hiển thị đẹp hơn
        matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        splitPane.setLeftComponent(new JScrollPane(matchesList));

        // 2. Panel bên phải: Hiển thị chi tiết các nước đi
        movesTextArea = new JTextArea("Chọn một ván đấu từ danh sách bên trái.");
        movesTextArea.setEditable(false);
        // Đảm bảo sử dụng font Monospaced để căn chỉnh hoạt động đúng
        movesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        splitPane.setRightComponent(new JScrollPane(movesTextArea));

        splitPane.setDividerLocation(350); // Thiết lập độ rộng ban đầu cho panel trái

        // 3. Thêm sự kiện để xử lý khi người dùng chọn một ván đấu
        matchesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Chỉ xử lý khi lựa chọn đã ổn định
                Match selectedMatch = matchesList.getSelectedValue();
                if (selectedMatch != null) {
                    displayMatchDetails(selectedMatch);
                }
            }
        });
    }

    /**
     * Tải lại dữ liệu từ DB và hiển thị lên danh sách.
     */
    public void loadAndDisplayHistory() {
        listModel.clear();
        List<Match> matches = matchDAO.getAllMatches();
        for (Match match : matches) {
            listModel.addElement(match);
        }
        movesTextArea.setText("Chọn một ván đấu từ danh sách bên trái.");
        movesTextArea.setCaretPosition(0); // Đảm bảo cuộn lên đầu khi tải lại
    }

    /**
     * Hiển thị chi tiết nước đi của ván đấu đã chọn, căn chỉnh đẹp hơn.
     */
    private void displayMatchDetails(Match match) {
        List<String> moves = moveDAO.getMovesForMatch(match.getMatchId());
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Ván đấu ID: %d\n", match.getMatchId()));
        sb.append(String.format("%s (Trắng) vs %s (Đen)\n", match.getPlayerWhiteName(), match.getPlayerBlackName()));
        sb.append(String.format("Kết quả: %s\n\n", match.getResult()));
        sb.append("Lịch sử nước đi:\n");
        sb.append("---------------------\n"); // Dòng phân cách

        int moveNumber = 1;
        for (int i = 0; i < moves.size(); i += 2) {
            // Định dạng: Số thứ tự (3 ký tự, căn phải), dấu chấm, cách,
            // Nước đi Trắng (10 ký tự, căn trái), cách, Nước đi Đen
            sb.append(String.format("%3d. %-10s ", moveNumber, moves.get(i)));

            // Nếu có nước đi của Đen
            if (i + 1 < moves.size()) {
                sb.append(moves.get(i + 1));
            }

            sb.append("\n"); // Xuống dòng cho cặp nước đi tiếp theo
            moveNumber++;
        }

        movesTextArea.setText(sb.toString());
        movesTextArea.setCaretPosition(0); // Luôn cuộn lên đầu
    }

    /**
     * Lớp nội bộ để tùy chỉnh cách hiển thị mỗi mục trong JList.
     */
    private static class MatchListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Match) {
                Match match = (Match) value;
                // Định dạng chuỗi hiển thị trên danh sách
                setText(String.format("ID %d: %s vs %s", match.getMatchId(), match.getPlayerWhiteName(),
                        match.getPlayerBlackName()));
            }
            return this;
        }
    }
}