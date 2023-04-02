package GameMVC;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;

public class ChatView extends JPanel implements GameModelSubscriber{

    GameController gameController;

    GameModel model;

    Boolean host;

    JTextField text;

    JTextArea textArea = new JTextArea(10, 30);

    JPanel panel;
    JScrollPane scrollPane;
    public ChatView(Boolean host){

        this.host = host;
        this.panel = new JPanel();

        scrollPane = new JScrollPane(textArea);


        scrollPane.setPreferredSize(new Dimension(300,400));

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        text = new JTextField();
        text.setPreferredSize(new Dimension(300,50));
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        textArea.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
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
//        panel.removeAll();
//        this.panel.setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        model.getChatMessage().forEach( s -> {
//            JLabel label = new JLabel(s);
//            label.setVerticalAlignment(SwingConstants.TOP);
//            gbc.gridx = 0;
//            gbc.gridy += 1;
//            gbc.anchor = GridBagConstraints.NORTHWEST;
//            gbc.fill = GridBagConstraints.NONE;
//            gbc.weightx = 1.0;
//            gbc.weighty = 1.0;
//            this.panel.add(label, gbc);
//        });
        textArea.setText("");
        model.getChatMessage().forEach( s -> {
            textArea.setText(textArea.getText() + s + "\n");
        });
        this.revalidate();
        this.repaint();

    }
}
