package GameClientMVC;

import java.awt.event.ActionEvent;

public class ClientGameController {

    ClientGameModel clientGameModel;

    public ClientGameController() {

    }

    public void setModel(ClientGameModel newClientGameModel) {
        clientGameModel = newClientGameModel;
    }

    public void handleTileClick(ActionEvent e, int row, int col) {
        clientGameModel.addTileClick(row, col);
    }
}
