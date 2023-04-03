package GameMVC;//import Game.GamePanelHost;
import GameMVC.HostGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.*;
import java.io.*;
import java.util.Objects;

public class HostMenu extends JPanel {

    MainMenu mainMenu;
    JFrame frame;
    JButton mainMenuButton;

    JTextField nameTextField = new JTextField("Enter Your Username");

    /* TextField for user to enter port Number they will connect to client with */
    JTextField portNumberTextField = new JTextField("Enter Port Number");

    HostGame hostGame;

    ServerSocket serverSocket;

    public HostMenu(JFrame frame) {

        this.frame = frame;



        /* Button to go back to main menu */
        mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setPreferredSize(new Dimension(350, 100));
        mainMenuButton.setFont(new Font("",Font.PLAIN,20));

        /* Once the user clicks to add text into the port number text field,
        the pre-written text will disappear */
        portNumberTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Objects.equals(portNumberTextField.getText(), "Enter Port Number")){
                    portNumberTextField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                /* Do nothing */
                if (Objects.equals(portNumberTextField.getText(), "")){
                    portNumberTextField.setText("Enter Port Number");

                }
            }
        });

        /* TextField for user to enter their Name */

        nameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

                if (Objects.equals(nameTextField.getText(), "Enter Your Username")){
                    nameTextField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Objects.equals(nameTextField.getText(), "")){
                    nameTextField.setText("Enter Your Username");
                }
            }
        });

        /* Connect Button that the user will press once they have entered their info to try
        and connect with the client user */
        JButton connectButton = new JButton("Connect");
        connectButton.setFont(new Font("",Font.PLAIN,20));
        connectButton.setBackground(Color.GREEN);
        connectButton.setPreferredSize(new Dimension(350, 100));

        /* Leo, please add comments here to explain what you did */
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        /* Leo, please add comments here to explain what you did */
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(portNumberTextField,gbc);

        /* Leo, please add comments here to explain what you did */
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(nameTextField,gbc);

        /* When the user clicks the connect button, the user becomes the host and will
        try to establish a TCP connection */
        connectButton.addActionListener(e -> {
            try {
                Connect();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        /* Leo, please add comments here to explain what you did */
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth = 2;
        this.add(connectButton,gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth = 2;
        this.add(mainMenuButton,gbc);


    }

    /* Gives host menu object the main menu panel to allow the user to switch back to main menu */
    public void addMainPanel(MainMenu mainPanel){
        this.mainMenu = mainPanel;
        mainMenuButton.addActionListener(e->{
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

        /* Leo, later we will set this number to be inputed from the user */
        hostGame = new HostGame(true);
        hostGame.setMainMenu(mainMenu);
        hostGame.setFrame(frame);
        System.out.println("Server started.");
        int portNumber = 30000;
        if (serverSocket!=null){
            serverSocket.close();
        }

        if (isNumeric(portNumberTextField.getText()) &&
                (Integer.parseInt(portNumberTextField.getText()) >= 30000) &&
                (Integer.parseInt(portNumberTextField.getText()) <= 40000)){
            portNumber = Integer.parseInt(portNumberTextField.getText());
        }

        try{
            serverSocket = new ServerSocket(portNumber);
        }catch (Exception e) {
            System.out.println("Error: server socket can't work with port "+portNumber);
            return;
        }

        /* Timeout to accept client for game in milliseconds, 1 s = 1000 ms */
        serverSocket.setSoTimeout(10000);

        try {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client socket accepted in server");

            /* Send client socket to the HostGame object */
            hostGame.addClientSocket(clientSocket);
            hostGame.addServerSocket(serverSocket);

            if (! Objects.equals(nameTextField.getText(), "Enter Your Username")){
                hostGame.setHostUsername(nameTextField.getText());
            }

            /* Create the thread to start messaging with the client */
            hostGame.startMessaging();

            this.frame.getContentPane().removeAll();
            this.frame.getContentPane().add(hostGame);
            this.frame.revalidate();
            this.frame.repaint();

        } catch (SocketTimeoutException e) {
            /* Hande timeout */

        }
    }
}
