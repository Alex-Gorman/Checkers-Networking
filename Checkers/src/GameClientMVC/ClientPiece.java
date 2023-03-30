package GameClientMVC;

import java.awt.*;

public class ClientPiece {
    Boolean player;
    Color color;
    Boolean king;
    int row;
    int col;

    public ClientPiece(int row, int col, Boolean bool, Color color) {
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
}
