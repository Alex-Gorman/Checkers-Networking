package GameMVC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;

public class ChatView extends JPanel implements GameModelSubscriber{

    GameController gameController;

    GameModel model;

    Boolean host;

    JTextField text;

    static DataOutputStream dout;

    JScrollPane scrollPane;
    public ChatView(Boolean host){
        this.host = host;
        JPanel panel = new JPanel();
        JLabel[] labels=new JLabel[8]; //callout.getRedCount() = 8
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        for (int i=0;i<8;i++){ //callout.getRedCount() = 8
            labels[i]=new JLabel("Red" + i);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            panel.add(labels[i], gbc);
        }
        this.scrollPane = new JScrollPane(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
//        panel.setBackground(Color.BLUE);
        scrollPane.setPreferredSize(new Dimension(600,100));
        scrollPane.setViewportView(panel);

        text = new JTextField();
        text.setPreferredSize(new Dimension(600,50));
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));

        JButton send = new JButton("Send");
        send.setPreferredSize(new Dimension(50,30));
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(e -> {
            try {
                gameController.handleSend(e, text.getText());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));

        this.add(scrollPane);
        this.add(text);
        this.add(send);
    }

    public void setModel(GameModel newGameModel) {
        model = newGameModel;
    }
    public void setController(GameController newController) {
        this.gameController = newController;
    }
    @Override
    public void modelUpdated() {

    }
}
