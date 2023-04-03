package GameMVC;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
        send.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
        send.setPreferredSize(new Dimension(80,30));
        send.addActionListener(e -> {
            try {
                gameController.handleSend(text.getText(), host);
                text.setText("");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        send.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        gameController.handleSend(text.getText(), host);
                        text.setText("");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        gameController.handleSend(text.getText(), host);
                        text.setText("");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        this.setBackground(new Color(159,235,237,0)); // Set the background color to red
        text.setBackground(new Color(243,221,188));
        textArea.setBackground(new Color(243,221,188));

        this.setFocusable(true);
        this.requestFocusInWindow();

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

        textArea.setText("");
        model.getChatMessage().forEach( s -> {
            textArea.setText(textArea.getText() + s + "\n");
        });
        this.revalidate();
        this.repaint();

    }

    public String getText() {
        return text.getText();
    }
}
