package GameHostMVC;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Game extends JPanel {

    Socket clientSocket;

    GameModel gameModel;

    public Game() {

        /* MVC Setup */
        GameView hostGameView = new GameView();
        gameModel = new GameModel();
        GameController gameController = new GameController();
        hostGameView.setModel(gameModel);
        hostGameView.setController(gameController);
        gameController.setModel(gameModel);
        gameModel.addSubscriber(hostGameView);

        hostGameView.initializeBoardChips();

        hostGameView.setPreferredSize(new Dimension(600, 600));
        this.add(hostGameView);
    }

    public void addClientSocket(Socket fd) {
        clientSocket = fd;
    }

    public void startMessaging() {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }

    public class MyRunnable implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("got to run()");
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                byte[] buffer = new byte[1024];
                int numBytes;
                String s = "";

                while (true) {
                    Thread.sleep(5000);
                    if (gameModel.getCurrentState() != GameModel.State.OTHER_PLAYER) {
                        continue;
                    }
                    System.out.println("GOT HERE HOST GAME 1");
                    s = gameModel.getMessageToSend();
                    System.out.println("msg to send"+s);
                    outputStream.write(s.getBytes());
                    gameModel.clearMessageToSendString();

                    numBytes = inputStream.read(buffer);
                    String message = new String(buffer, 0, numBytes);

                    System.out.println("Message Received from client");

                    gameModel.takeIncomingMove(message);

                    /* Set the player state to FIRST_PRESS */
                    gameModel.setPlayerStateToTheirTurn();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
