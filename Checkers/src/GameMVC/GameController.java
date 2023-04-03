package GameMVC;

import java.awt.event.ActionEvent;
import java.util.Objects;

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

    public void handleSend(String msg, Boolean isHost){
        if (!Objects.equals(msg, "")){
            gameModel.sendChatMessage(msg, isHost);
        }
    }

    public void quitGame(){
        gameModel.toMainMenu();
        try {
            gameModel.socket.close();
        }catch (Exception e){}
    }
}
