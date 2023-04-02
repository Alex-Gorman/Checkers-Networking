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

    JPanel panel;
    JScrollPane scrollPane;
    public ChatView(Boolean host){

        this.host = host;
        this.panel = new JPanel();

        this.scrollPane = new JScrollPane(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setPreferredSize(new Dimension(600,100));
        scrollPane.setViewportView(panel);

        text = new JTextField();
        text.setPreferredSize(new Dimension(600,50));
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));

        JButton send = new JButton("Send");
        send.setPreferredSize(new Dimension(50,20));
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(e -> {
            try {
                gameController.handleSend(e, text.getText(), host);
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
        panel.removeAll();
        this.panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        model.getChatMessage().forEach( s -> {
            JLabel label = new JLabel(s);
            label.setVerticalAlignment(SwingConstants.TOP);
            gbc.gridx = 0;
            gbc.gridy += 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            this.panel.add(label, gbc);
        });
        panel.revalidate();
        panel.repaint();

    }
}
