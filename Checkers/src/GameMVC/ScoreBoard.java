package GameMVC;

import javax.swing.*;
import java.awt.*;

public class ScoreBoard extends JPanel implements GameModelSubscriber {
    GameController gameController;

    GameModel model;

    JLabel player1 ;
    JLabel player2 ;
    public ScoreBoard(){

        player1 = new JLabel("Player1: " );
        player2 = new JLabel("Player2: " );

        player1.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        player2.setFont(new Font("SAN_SERIF", Font.BOLD, 20));

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,0,5,7);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        this.add(player1,gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(player2,gbc);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(new Color(243,221,188));
    }

    public void setModel(GameModel newGameModel) {
        model = newGameModel;
        player1.setText(model.hostName + ": " + model.hostScore);
        player2.setText(model.clientName + ": " + model.clientScore);

    }

    public void setController(GameController newController) {
        this.gameController = newController;
    }

    @Override
    public void modelUpdated() {
        player1.setText(model.hostName + ": " + model.hostScore);
        player2.setText(model.clientName + ": " + model.clientScore);
        this.revalidate();
        this.repaint();
    }
}
