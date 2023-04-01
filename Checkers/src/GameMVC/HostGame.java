package GameMVC;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HostGame extends JPanel {

    Socket clientSocket;

    GameModel gameModel;

    public HostGame(Boolean host) {

        /* MVC Setup */
        GameView gameView = new GameView(host);
        gameModel = new GameModel();
        GameController gameController = new GameController();
        gameView.setModel(gameModel);
        gameView.setController(gameController);
        gameController.setModel(gameModel);
        gameModel.addSubscriber(gameView);

        gameView.initializeBoardChips();

        gameView.setPreferredSize(new Dimension(600, 600));
        ChatView chatView = new ChatView(false);
        chatView.setModel(gameModel);
        chatView.setController(gameController);
        gameModel.addSubscriber(chatView);
        chatView.setPreferredSize(new Dimension(600, 200));

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(gameView,gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(chatView,gbc);

        gameModel.addSocket(clientSocket);
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
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                byte[] buffer = new byte[1024];
                int numBytes;
                String s = "";

                while (true) {
                    Thread.sleep(100);
//                    System.out.println(gameModel.getCurrentState());
                    if (gameModel.getCurrentState() != GameModel.State.OTHER_PLAYER) {
                        ;
                        Thread.sleep(100);
                    }
                    else {
//                        System.out.println("GOT HERE HOST GAME 1");
                        s = gameModel.getMessageToSend();
                        System.out.println("msg to send"+s);
                        outputStream.write(s.getBytes());
                        gameModel.clearMessageToSendString();

                        numBytes = inputStream.read(buffer);
                        String message = new String(buffer, 0, numBytes);

//                        System.out.println("Message Received from client");

                        gameModel.takeIncomingMove(message);


                        gameModel.canJump();

                        /* Set the player state to FIRST_PRESS */
//                        gameModel.setPlayerStateToTheirTurn();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}
