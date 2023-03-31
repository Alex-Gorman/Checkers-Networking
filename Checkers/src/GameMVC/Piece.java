package GameMVC;

import java.awt.*;

public class Piece {
    Boolean player;
    Color color;
    Boolean king;
    int row;
    int col;

    public Piece(int row, int col, Boolean bool, Color color) {
        this.player = bool;
        this.color = color;
        this.king = false;
        this.row = row;
        this.col = col;
    }

    public void setKing() {
        this.king = true;
    }

    public void updateRowCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Boolean isKing() {
        return king;
    }
}
