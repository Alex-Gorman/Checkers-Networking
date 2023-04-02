package GameMVC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class GameTile extends JButton {

    int row, col;
    Piece piece;

    GameView gameView;

    Color originalColor;

    public GameTile(int row, int col, GameView gameView) {

        /* Set the row and col index */
        this.row = row;
        this.col = col;

        /* Set the color of the tile */
        if (!(row % 2 != 0 && col % 2 == 0 || row % 2 == 0 && col % 2 != 0)) {
            Color customGreen = new Color(63, 171, 52);
            this.setBackground(new Color(139,69,19));
            originalColor = new Color(139,69,19);
        } else {
            this.setBackground(Color.WHITE);
            originalColor = Color.WHITE;
        }
        /* Set to true to enabled action on press */
        this.setEnabled(true);

        /* Set the GameView object */
        this.gameView = gameView;

        /* Add an actionListener to the button */
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Send coordinates of tile pressed to the GameView object to handle it */
                gameView.takeButtonData(e, row, col);
            }
        });
    }

    public void assignPiece(Piece p) {
        piece = p;

        /* If not king */
        if (!p.isKing()) {
            ImageIcon icon = new ImageIcon(createCircleImage(p.color));
            this.setIcon(icon);
        }
        else {
            ImageIcon icon = new ImageIcon(createCircleKingImage(p.color));
//            ImageIcon icon = new ImageIcon(createCircleImage(Color.PINK));
            this.setIcon(icon);
        }
    }

    public void assignPieceToBeKing(Piece p) {
        piece  = p;
    }

    public void clearPiece() {
        piece = null;
        this.setIcon(null);
    }

    private static BufferedImage createCircleImage(Color color) {
        int circleSize = 55;
        BufferedImage image = new BufferedImage(circleSize, circleSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillOval(0, 0, circleSize, circleSize);
        g.dispose();
        return image;
    }

    private static BufferedImage createCircleKingImage(Color color) {
        int circleSize = 55;
        BufferedImage image = new BufferedImage(circleSize, circleSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillOval(0, 0, circleSize, circleSize);
        g.setColor(new Color(255,215,0));
        int innerCircleSize = circleSize * 1/2;
        int innerCircleOffset = (circleSize - innerCircleSize) / 2;
        g.fillOval(innerCircleOffset, innerCircleOffset, innerCircleSize, innerCircleSize);
        g.dispose();
        return image;
    }

    public void setBackgroundToOriginalColor() {
        this.setBackground(originalColor);
    }



}
