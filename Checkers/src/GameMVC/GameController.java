package GameMVC;

import GameMVC.GameModel;

import java.awt.event.ActionEvent;

public class GameController {

    GameModel gameModel;

    public GameController() {
    }

    public void setModel(GameModel newGameModel) {
        gameModel = newGameModel;
    }

    public void handleTileClick(ActionEvent e, int row, int col) {
        gameModel.addTileClick(row, col);
    }

    // Leo Add
    public void handleSend(ActionEvent e, String msg){
        gameModel.sendMessage(msg);
    }
}
