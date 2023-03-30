package GameHostMVC;

import java.awt.*;
import java.awt.event.ActionEvent;

public class GameView extends Panel implements GameModelSubscriber {
    GameModel model;

//    GameTile[][] tiles = new GameTile[8][8];

    GameController gameController;

    public GameView() {

        /* 8 x 8 grid layout */
        this.setLayout(new GridLayout(8, 8));
    }

    public void initializeBoardChips() {
        /* Create the tiles of the game board */
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                model.getTiles()[row][col] = new GameTile(row, col, this);
                this.add(model.getTiles()[row][col]);
            }
        }

        /* Set the black player (2) moves */
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    model.addPiecePlayerTwo(new Piece(row, col, false, Color.BLACK));
                }
            }
        }

        /* Set the red player (1) moves */
        for (int row = 5; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    model.addPiecePlayerOne(new Piece(row, col, true, Color.RED));
                }
            }
        }
        modelUpdated();
    }

    public void setModel(GameModel newGameModel) {
        model = newGameModel;
    }

    public void takeButtonData(ActionEvent e, int row, int col) {
        gameController.handleTileClick(e, row, col);
    }

    public void setController(GameController newController) {
//        b.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                newController.handleClick(e);
//            }
//        });
        this.gameController = newController;
    }

    @Override
    public void modelUpdated() {

        /* Clear all the piece from the board */
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                model.getTiles()[row][col].clearPiece();
                model.getTiles()[row][col].setBackgroundToOriginalColor();
            }
        }

        /* Add all the pieces back with updated positions */
        /* Player One */
        model.getPlayerOnePieces().forEach(player -> {
            model.getTiles()[player.row][player.col].assignPiece(player);
        });

        /* Player Two */
        model.getPlayerTwoPieces().forEach(player -> {
            model.getTiles()[player.row][player.col].assignPiece(player);
        });

        /* Tiles player can move to */
        model.getTilesPlayerCanMoveTo().forEach(tile -> tile.setBackground(Color.ORANGE));


    }
}

