package com.ChessPTIT.view;

/**
 * Interface này định nghĩa một hợp đồng cho bất kỳ lớp nào
 * có khả năng chuyển đổi giữa các panel.
 */
public interface PanelSwitcher {
    void switchToPanel(String panelName);
}