import javax.management.remote.JMXConnectorFactory;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
public class ClientMenu extends JPanel {

    JPanel mainPanel;
    JFrame frame;
    JButton mainMenuButton = new JButton("Main Menu");

    public ClientMenu(JFrame frame){
        this.frame =frame;
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

    public void addMainPanel(JPanel mainPanel){
        this.mainPanel = mainPanel;
        mainMenuButton.addActionListener(e->{
            frame.getContentPane().removeAll();
            frame.getContentPane().add(mainPanel);
            frame.revalidate();
            frame.repaint();
        });
    }
    private void Connect() throws IOException {
//        Socket socket = null;
//        PrintWriter out = null;
//        BufferedReader in = null;
//
//        try {
//            socket = new Socket("10.136.238.183", 30000);
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        } catch (UnknownHostException e) {
//            System.err.println("Unknown host: 10.136.238.183");
//            System.exit(1);
//        } catch (IOException e) {
//            System.err.println("Couldn't get I/O for the connection to: localhost.");
//            System.exit(1);
//        }
//
//        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//        String userInput;
//        while ((userInput = stdIn.readLine()) != null) {
//            out.println(userInput);
//            System.out.println("Server: " + in.readLine());
//            if (userInput.equals("Bye."))
//                break;
//        }
//
//        out.close();
//        in.close();
//        stdIn.close();
//        socket.close();
//        String SERVER_ADDRESS = "10.136.238.183";
        String SERVER_ADDRESS = "127.0.1.1";
        int SERVER_PORT = 30000;
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

        System.out.println("Connected to server");

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter a message to send to the server: ");
            String message = scanner.nextLine();

            // Send the message to the server
            outputStream.write(message.getBytes());

            // Receive the response from the server
            byte[] buffer = new byte[1024];
            int numBytes = inputStream.read(buffer);
            String response = new String(buffer, 0, numBytes);
            System.out.println("Received response from server: " + response);
        }
    }


}
