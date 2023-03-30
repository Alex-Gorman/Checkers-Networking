package GameMVC;

import GameMVC.GameView;
import GameMVC.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class GameTile extends JButton {

    int row, col;
    Piece piece;

    GameView hostGameView;

    Color originalColor;

    public GameTile(int row, int col, GameView hostGameView) {

        /* Set the row and col index */
        this.row = row;
        this.col = col;

        /* Set the color of the tile */
        if (!(row % 2 != 0 && col % 2 == 0 || row % 2 == 0 && col % 2 != 0)) {
            Color customGreen = new Color(63, 171, 52);
            this.setBackground(Color.GREEN);
            originalColor = Color.GREEN;
        } else {
            this.setBackground(Color.WHITE);
            originalColor = Color.WHITE;
        }
        /* Set to true to enabled action on press */
        this.setEnabled(true);

        /* Set the GameView object */
        this.hostGameView = hostGameView;

        /* Add an actionListener to the button */
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Send coordinates of tile pressed to the GameView object to handle it */
                hostGameView.takeButtonData(e, row, col);
            }
        });
    }

    public void assignPiece(Piece p) {
        piece = p;
        ImageIcon icon = new ImageIcon(createBlackCircleImage(p.color));
        this.setIcon(icon);
    }

    public void clearPiece() {
        piece = null;
        this.setIcon(null);
    }

    private static BufferedImage createBlackCircleImage(Color color) {
        int circleSize = 55;
        BufferedImage image = new BufferedImage(circleSize, circleSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillOval(0, 0, circleSize, circleSize);
        g.dispose();
        return image;
    }

    public void setBackgroundToOriginalColor() {
        this.setBackground(originalColor);
    }



}
