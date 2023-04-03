package GameMVC;

import GameMVC.Piece;
import GameMVC.GameController;
import GameMVC.GameModel;
import GameMVC.GameModelSubscriber;
import GameMVC.GameTile;
import GameMVC.Piece;

import java.awt.*;
import java.awt.event.ActionEvent;

public class GameView extends Panel implements GameModelSubscriber {

    GameController gameController;

    GameModel model;

    Boolean host;

    Color black = new Color(21,21,21,255);
    Color white = new Color(225,220,185,255);

    public GameView(Boolean host) {

        this.host = host;

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

        if (host) initializeHostGameBoard();
        else initializeClientGameBoard();

        modelUpdated();
    }

    public void setModel(GameModel newGameModel) {
        model = newGameModel;
    }

    public void takeButtonData(ActionEvent e, int row, int col) {
        gameController.handleTileClick(e, row, col);
    }

    public void setController(GameController newController) {
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
        model.getTilesPlayerCanMoveTo().forEach(tile -> {
            tile.setBackground(Color.ORANGE);
            System.out.println("painted orange");
        });

        model.tilesOfPiecesThatCanJump.forEach(tile -> tile.setBackground(Color.cyan));
    }

    public void initializeHostGameBoard() {

//        /* Set the black player (2) moves */
//        for (int row = 0; row < 3; row++) {
//            for (int col = 0; col < 8; col++) {
//                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
//                    model.addPiecePlayerTwo(new Piece(row, col, false, white));
//                }
//            }
//        }
//
//        /* Set the red player (1) moves */
//        for (int row = 5; row < 8; row++) {
//            for (int col = 0; col < 8; col++) {
//                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
//                    model.addPiecePlayerOne(new Piece(row, col, true, black));
//                }
//            }
//        }



        model.addPiecePlayerTwo(new Piece(5, 3, false, Color.BLUE));
//        model.addPiecePlayerTwo(new Piece(4, 4, false, Color.BLUE));
        model.addPiecePlayerOne(new Piece(7, 1, true, Color.RED));

//        model.addPiecePlayerTwo(new Piece(6, 1, false, Color.BLACK));
//        model.addPiecePlayerOne(new Piece(7, 2, true, Color.RED));

        /* Test using this */
//        model.addPiecePlayerTwo(new Piece(0, 3, false, Color.BLACK));
//        model.addPiecePlayerOne(new Piece(3, 6, true, Color.RED));
//        model.addPiecePlayerTwo(new Piece(6, 5, false, Color.BLACK));
    }

    public void initializeClientGameBoard() {

        model.addPiecePlayerTwo(new Piece(0, 6, false, Color.RED));
//        model.addPiecePlayerOne(new Piece(3, 3, true, Color.BLUE));
        model.addPiecePlayerOne(new Piece(2, 4, true, Color.BLUE));

//        model.addPiecePlayerTwo(new Piece(0, 5, false, Color.RED));
//        model.addPiecePlayerOne(new Piece(1, 6, true, Color.BLACK));

        /* Test using this */
//        model.addPiecePlayerTwo(new Piece(4, 1, false, Color.RED));
//        model.addPiecePlayerOne(new Piece(7, 4, true, Color.BLACK));
//        model.addPiecePlayerOne(new Piece(1, 2, true, Color.BLACK));



//        /* Set the black player (2) moves */
//        for (int row = 0; row < 3; row++) {
//            for (int col = 0; col < 8; col++) {
//                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
//                    model.addPiecePlayerTwo(new Piece(row, col, false, black));
//                }
//            }
//        }
//
//        /* Set the red player (1) moves */
//        for (int row = 5; row < 8; row++) {
//            for (int col = 0; col < 8; col++) {
//                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
//                    model.addPiecePlayerOne(new Piece(row, col, true, white));
//                }
//            }
//        }
    }


}

