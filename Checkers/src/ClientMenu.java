import GameMVC.ClientGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ClientMenu extends JPanel {

    JPanel mainPanel;
    JFrame frame;
    JButton mainMenuButton = new JButton("Main Menu");

    JTextField userName = new JTextField("Enter Your Username");

    ClientGame clientGame;
    public ClientMenu(JFrame frame) {

        this.frame = frame;

        clientGame = new ClientGame();
        mainMenuButton.setPreferredSize(new Dimension(350, 100));
        mainMenuButton.setFont(new Font("",Font.PLAIN,20));
        JButton connect = new JButton("Connect");
        connect.setFont(new Font("",Font.PLAIN,20));
        connect.setBackground(Color.GREEN);
        connect.setPreferredSize(new Dimension(350, 100));        JTextField hostPortField = new JTextField("Enter Host Port Number");

        JTextField hostIpField = new JTextField("Enter Host IP Address");

        hostPortField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hostPortField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                /* Do nothing */
            }
        });

        hostIpField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hostIpField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                /* Do nothing */
            }
        });

        userName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                userName.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                /* Do nothing */
            }
        });
        /* Leo, please add comments here to explain what you did */
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

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

    public void addMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
        mainMenuButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(mainPanel);
            frame.revalidate();
            frame.repaint();
        });
    }

    private void Connect() throws IOException {
        String SERVER_ADDRESS = "127.0.1.1";
        int SERVER_PORT = 30000;
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

        clientGame.addSocket(socket);
//        clientGame.setClientUsername(nameTextField.getText());
        if (! Objects.equals(userName.getText(), "Enter Your Username")){
            clientGame.setClientUsername(userName.getText());
        }

        /* Create the thread to start messaging with the host */
        clientGame.startMessaging();

        this.frame.getContentPane().removeAll();
        this.frame.getContentPane().add(clientGame);
        this.frame.revalidate();
        this.frame.repaint();
    }
}

//    private void Connect() throws IOException {
//
//        String SERVER_ADDRESS = "127.0.1.1";
//        int SERVER_PORT = 30000;
//        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//
//        System.out.println("Connected to server");
//
//        InputStream inputStream = socket.getInputStream();
//        OutputStream outputStream = socket.getOutputStream();
//
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//            System.out.print("Enter a message to send to the server: ");
//            String message = scanner.nextLine();
//
//            // Send the message to the server
//            outputStream.write(message.getBytes());
//
//            // Receive the response from the server
//            byte[] buffer = new byte[1024];
//            int numBytes = inputStream.read(buffer);
//            String response = new String(buffer, 0, numBytes);
//            System.out.println("Received response from server: " + response);
//        }
//    }
//}
