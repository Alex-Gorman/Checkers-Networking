//import Game.GamePanelHost;
import GameMVC.HostGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.*;
import java.io.*;
import java.util.Objects;

public class HostMenu extends JPanel {

    JPanel mainMenu;
    JFrame frame;
    JButton mainMenuButton;

    JTextField nameTextField = new JTextField("Enter Your Username");

    HostGame hostGame;

    public HostMenu(JFrame frame) {

        this.frame = frame;

        /* Create the game object and set host to true */
        hostGame = new HostGame(true);

        /* TextField for user to enter port Number they will connect to client with */
        JTextField portNumberTextField = new JTextField("Enter Port Number");

        /* Button to go back to main menu */
        mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setPreferredSize(new Dimension(350, 100));
        mainMenuButton.setFont(new Font("",Font.PLAIN,20));

        /* Once the user clicks to add text into the port number text field,
        the pre-written text will disappear */
        portNumberTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                portNumberTextField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                /* Do nothing */
            }
        });

        /* TextField for user to enter their Name */

        nameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                nameTextField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                /* Do nothing */
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
    public void addMainPanel(JPanel mainPanel){
        this.mainMenu = mainPanel;
        mainMenuButton.addActionListener(e->{
            frame.getContentPane().removeAll();
            frame.getContentPane().add(mainPanel);
            frame.revalidate();
            frame.repaint();
        });
    }

    private void Connect() throws IOException {

        /* Leo, later we will set this number to be inputed from the user */
        int portNumber = 30000;

        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server started.");

        /* Timeout to accept client for game in milliseconds, 1 s = 1000 ms */
        serverSocket.setSoTimeout(10000);

        try {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client socket accepted in server");

            /* Send client socket to the HostGame object */
            hostGame.addClientSocket(clientSocket);
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


//    private void Connect() throws IOException {
//        ServerSocket serverSocket = new ServerSocket(30000);
//        System.out.println("Server started on port " + 30000);
//
//        while (true) {
//            Socket clientSocket = serverSocket.accept();
//            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
//            frame.getContentPane().removeAll();
//            frame.getContentPane().add(this.mainMenu);
//            frame.revalidate();
//            frame.repaint();
//            Thread clientThread = new Thread(() -> handleClient(clientSocket));
//            clientThread.start();
//        }
//    }
//    private static void handleClient(Socket clientSocket) {
//        try {
//            InputStream inputStream = clientSocket.getInputStream();
//            OutputStream outputStream = clientSocket.getOutputStream();
//
//            byte[] buffer = new byte[1024];
//            int numBytes;
//
//            while ((numBytes = inputStream.read(buffer)) != -1) {
//                String message = new String(buffer, 0, numBytes);
//                System.out.println("Received message from client: " + message);
//                Scanner scanner = new Scanner(System.in);
//                System.out.print("Enter a message to send to the server: ");
//
//                String message2 = scanner.nextLine();
//
//                // Send the message back to the client
//                outputStream.write(message2.getBytes());
//            }
//            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
//            clientSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
