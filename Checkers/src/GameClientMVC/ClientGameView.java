package GameClientMVC;

import java.awt.*;
import java.awt.event.ActionEvent;

public class ClientGameView extends Panel implements ClientGameModelSubscriber {
    ClientGameModel model;
    ClientGameController clientGameController;

    public ClientGameView() {

        /* 8 x 8 grid layout */
        this.setLayout(new GridLayout(8, 8));
    }

    public void initializeBoardChips() {
        /* Create the tiles of the game board */
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                model.getTiles()[row][col] = new ClientGameTile(row, col, this);
                this.add(model.getTiles()[row][col]);
            }
        }

        /* Set the black player (2) moves */
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    model.addPiecePlayerTwo(new ClientPiece(row, col, false, Color.RED));
                }
            }
        }

        /* Set the red player (1) moves */
        for (int row = 5; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    model.addPiecePlayerOne(new ClientPiece(row, col, true, Color.BLACK));
                }
            }
        }
        modelUpdated();
    }

    public void setModel(ClientGameModel newClientGameModel) {
        model = newClientGameModel;
    }

    public void takeButtonData(ActionEvent e, int row, int col) {
        clientGameController.handleTileClick(e, row, col);
    }

    public void setController(ClientGameController newController) {
        this.clientGameController = newController;
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

