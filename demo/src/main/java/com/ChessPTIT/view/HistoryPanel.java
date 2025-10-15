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

    public HistoryPanel(PanelSwitcher switcher) { // Sửa constructor để nhận PanelSwitcher
        this.matchDAO = new MatchDAO();
        this.moveDAO = new MoveDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Nút Back
        JButton backButton = new JButton("Quay lại Menu");
        backButton.addActionListener(e -> switcher.switchToPanel(MainFrame.MENU_PANEL));
        add(backButton, BorderLayout.SOUTH);

        // --- Panel chính ---
        JSplitPane splitPane = new JSplitPane();
        add(splitPane, BorderLayout.CENTER);

        // 1. Panel bên trái: Hiển thị danh sách các ván đấu
        listModel = new DefaultListModel<>();
        matchesList = new JList<>(listModel);
        matchesList.setCellRenderer(new MatchListRenderer()); // Custom renderer để hiển thị đẹp hơn
        matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        splitPane.setLeftComponent(new JScrollPane(matchesList));

        // 2. Panel bên phải: Hiển thị chi tiết các nước đi
        movesTextArea = new JTextArea("Chọn một ván đấu để xem chi tiết.");
        movesTextArea.setEditable(false);
        movesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        splitPane.setRightComponent(new JScrollPane(movesTextArea));

        splitPane.setDividerLocation(300); // Điều chỉnh độ rộng ban đầu

        // 3. Thêm sự kiện khi chọn một ván đấu trong danh sách
        matchesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
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
        movesTextArea.setText("Chọn một ván đấu để xem chi tiết.");
    }

    private void displayMatchDetails(Match match) {
        List<String> moves = moveDAO.getMovesForMatch(match.getMatchId());
        StringBuilder sb = new StringBuilder();
        sb.append("Ván đấu ID: ").append(match.getMatchId()).append("\n");
        sb.append(match.getPlayerWhiteName()).append(" (Trắng) vs ").append(match.getPlayerBlackName())
                .append(" (Đen)\n");
        sb.append("Kết quả: ").append(match.getResult()).append("\n\n");
        sb.append("Các nước đi:\n");

        int moveNumber = 1;
        for (int i = 0; i < moves.size(); i += 2) {
            sb.append(moveNumber).append(". ").append(moves.get(i));
            if (i + 1 < moves.size()) {
                sb.append("\t").append(moves.get(i + 1));
            }
            sb.append("\n");
            moveNumber++;
        }
        movesTextArea.setText(sb.toString());
        movesTextArea.setCaretPosition(0); // Cuộn lên đầu
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
                setText(String.format("ID: %d - %s vs %s", match.getMatchId(), match.getPlayerWhiteName(),
                        match.getPlayerBlackName()));
            }
            return this;
        }
    }
}