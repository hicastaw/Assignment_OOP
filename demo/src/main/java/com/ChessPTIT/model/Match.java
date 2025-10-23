package com.ChessPTIT.model;

import java.util.ArrayList;
import java.util.List;

public class Match {
    private int matchId;
    private String playerWhiteName;
    private String playerBlackName;
    private String result;
    private List<String> moves;

    public Match(int matchId, String playerWhiteName, String playerBlackName, String result) {
        this.matchId = matchId;
        this.playerWhiteName = playerWhiteName;
        this.playerBlackName = playerBlackName;
        this.result = result;
        this.moves = new ArrayList<>();
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }
    public void setPlayerWhiteName(String playerWhiteName) {
        this.playerWhiteName = playerWhiteName;
    }

    public void setPlayerBlackName(String playerBlackName) {
        this.playerBlackName = playerBlackName;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }

    // Getters
    public int getMatchId() {
        return matchId;
    }

    public String getPlayerWhiteName() {
        return playerWhiteName;
    }

    public String getPlayerBlackName() {
        return playerBlackName;
    }

    public String getResult() {
        return result;
    }

    public List<String> getMoves() {
        return moves;
    }
}
