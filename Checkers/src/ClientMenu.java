import GameClientMVC.ClientGame;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ClientMenu extends JPanel {

    JPanel mainPanel;
    JFrame frame;
    JButton mainMenuButton = new JButton("Main Menu");

    ClientGame clientGame;
    public ClientMenu(JFrame frame) {

        this.frame = frame;

        clientGame = new ClientGame();

        JButton connect = new JButton("Connect");
        connect.setSize(150, 150);
        JTextField hostPortField = new JTextField("Enter Host Port Number");
        JTextField hostIpField = new JTextField("Enter Host IP Address");
        JTextField userName = new JTextField("Enter Your Username");
        this.add(hostIpField);
        this.add(hostPortField);
        this.add(userName);
        this.add(mainMenuButton);
        this.add(connect);
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
