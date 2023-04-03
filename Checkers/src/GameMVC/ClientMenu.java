package GameMVC;

import GameMVC.ClientGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ClientMenu extends JPanel {

    MainMenu mainPanel;
    JFrame frame;
    JButton mainMenuButton = new JButton("Main Menu");

    JTextField userName = new JTextField("Enter Your Username");
    JTextField hostIpField = new JTextField("Enter Host IP Address");
    JTextField hostPortField = new JTextField("Enter Port Number");

    ClientGame clientGame;
    public ClientMenu(JFrame frame) {

        this.frame = frame;



        mainMenuButton.setPreferredSize(new Dimension(350, 100));
        mainMenuButton.setFont(new Font("",Font.PLAIN,20));
        JButton connect = new JButton("Connect");
        connect.setFont(new Font("",Font.PLAIN,20));
        connect.setBackground(Color.GREEN);
        connect.setPreferredSize(new Dimension(350, 100));

        hostPortField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Objects.equals(hostPortField.getText(), "Enter Port Number")){
                    hostPortField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Objects.equals(hostPortField.getText(), "")){
                    hostPortField.setText("Enter Port Number");
                }
            }
        });

        hostIpField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Objects.equals(hostIpField.getText(), "Enter Host IP Address")){
                    hostIpField.setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Objects.equals(hostIpField.getText(), "")){
                    hostIpField.setText("Enter Host IP Address");
                }
            }
        });

        userName.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (Objects.equals(userName.getText(), "Enter Your Username")){
                    userName.setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Objects.equals(userName.getText(), "")){
                    userName.setText("Enter Your Username");
                }
            }
        });
        /* Leo, please add comments here to explain what you did */
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        setBackground(new Color(159,235,237)); // Set the background color to red

        /* Leo, please add comments here to explain what you did */
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(hostIpField,gbc);

        /* Leo, please add comments here to explain what you did */
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(hostPortField,gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        this.add(userName,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth = 2;
        this.add(connect,gbc);

        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth = 2;
        this.add(mainMenuButton,gbc);
        connect.addActionListener(e -> {
            try {
                Connect();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void addMainPanel(MainMenu mainPanel) {
        this.mainPanel = mainPanel;
        mainMenuButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(mainPanel);
            frame.revalidate();
            frame.repaint();
        });

    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void Connect() throws IOException {
        clientGame = new ClientGame();
        clientGame.setMainMenu(mainPanel);
        clientGame.setFrame(frame);
        String SERVER_ADDRESS = "127.0.1.1";
        int SERVER_PORT = 30000;
        if (isNumeric(hostPortField.getText()) &&
                (Integer.parseInt(hostPortField.getText()) >= 30000) &&
                (Integer.parseInt(hostPortField.getText()) <= 40000)){
            SERVER_PORT = Integer.parseInt(hostPortField.getText());
        }
        if (!Objects.equals(hostIpField.getText(), "Enter Host IP Address") && !Objects.equals(hostIpField.getText(),"")){
            SERVER_ADDRESS = hostIpField.getText();
        }

        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

        clientGame.addSocket(socket);
        if (! Objects.equals(userName.getText(), "Enter Your Username")){
            clientGame.setClientUsername(userName.getText());
        }

        /* Create the thread to start messaging with the host */
        clientGame.startMessaging();
        clientGame.sendInitMsg();

        this.frame.getContentPane().removeAll();
        this.frame.getContentPane().add(clientGame);
        this.frame.revalidate();
        this.frame.repaint();
    }
}
