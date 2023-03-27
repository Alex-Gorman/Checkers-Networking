import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class HostMenu extends JPanel {

    JPanel mainMenu;
    JFrame frame;
    JButton mainMenuButton = new JButton("Main Menu");

    public HostMenu(JFrame frame) {

        this.frame = frame;
        JTextField portNumberTextField = new JTextField("Enter Port Number");
        JTextField nameTextField = new JTextField("Enter Name");
        JButton connectButton = new JButton("Connect");

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(3,3,3,3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(portNumberTextField,gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(nameTextField,gbc);

        connectButton.addActionListener(e -> {
            try {
                Connect();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        connectButton.setBackground(Color.GREEN);

        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth = 2;
        this.add(connectButton,gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth = 2;
        this.add(mainMenuButton,gbc);
    }
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
//        ServerSocket serverSocket = null;
//        try {
//            serverSocket = new ServerSocket(30000);
//        } catch (IOException e) {
//            System.err.println("Could not listen on port 5000.");
//            System.exit(1);
//        }
//
//        Socket clientSocket = null;
//        try {
//            System.out.println("Waiting for connection...");
//            clientSocket = serverSocket.accept();
//            System.out.println("Connection established with " + clientSocket.getInetAddress().getHostAddress());
//        } catch (IOException e) {
//            System.err.println("Accept failed.");
//            System.exit(1);
//        }
//
//        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//        String inputLine;
//        while ((inputLine = in.readLine()) != null) {
//            System.out.println("Client: " + inputLine);
//            out.println(inputLine);
//            if (inputLine.equals("Bye."))
//                break;
//        }
//
//        out.close();
//        in.close();
//        clientSocket.close();
//        serverSocket.close();

        ServerSocket serverSocket = new ServerSocket(30000);
        System.out.println("Server started on port " + 30000);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
            frame.getContentPane().removeAll();
            frame.getContentPane().add(this.mainMenu);
            frame.revalidate();
            frame.repaint();
            Thread clientThread = new Thread(() -> handleClient(clientSocket));
            clientThread.start();
        }
    }
    private static void handleClient(Socket clientSocket) {



        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            byte[] buffer = new byte[1024];
            int numBytes;

            while ((numBytes = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, numBytes);
                System.out.println("Received message from client: " + message);
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter a message to send to the server: ");

                String message2 = scanner.nextLine();

                // Send the message back to the client
                outputStream.write(message2.getBytes());
            }

            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
