package GameClientMVC;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientGame extends JPanel {

    Socket socket;

    ClientGameModel clientGameModel;
    public ClientGame() {

        /* MVC Setup */
        ClientGameView clientGameView = new ClientGameView();
        clientGameModel = new ClientGameModel();
        ClientGameController clientGameController = new ClientGameController();
        clientGameView.setModel(clientGameModel);
        clientGameView.setController(clientGameController);
        clientGameController.setModel(clientGameModel);
        clientGameModel.addSubscriber(clientGameView);

        clientGameView.initializeBoardChips();

        clientGameView.setPreferredSize(new Dimension(600, 600));
        this.add(clientGameView);
    }

    public void addSocket(Socket fd) {
        socket = fd;
    }

    public void startMessaging() {
        Thread thread = new Thread(new ClientGame.MyRunnable());
        thread.start();
    }

    public class MyRunnable implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("got to run()");
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                byte[] buffer = new byte[1024];
                int numBytes;
                String s = "";

                while (true) {
                    Thread.sleep(5000);

                    System.out.println("GOT HERE CLIENT GAME 1");
                    numBytes = inputStream.read(buffer);
                    System.out.println("GOT HERE HOST GAME 2");
                    if (numBytes == -1) break;


                    String message = new String(buffer, 0, numBytes);
                    System.out.println(message);
                    if (!message.equals("")) clientGameModel.takeIncomingMove(message);

                    clientGameModel.setPlayerStateToTheirTurn();

                    while (clientGameModel.getCurrentState() != ClientGameModel.State.OTHER_PLAYER) {

                    }

                    System.out.println("Message sent to host");

                    s = clientGameModel.getMessageToSend();
                    outputStream.write(s.getBytes());

                    clientGameModel.clearMessageToSendString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Broke out of loop, buffer == -1");
        }
    }
}
