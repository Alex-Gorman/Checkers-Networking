package GameMVC;

import GameMVC.GameController;
import GameMVC.GameModel;
import GameMVC.GameView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientGame extends JPanel {

    Socket socket;

    GameModel gameModel;
    public ClientGame() {

        /* MVC Setup */
        GameView gameView = new GameView(false);
        gameModel = new GameModel(false);
        GameController gameController = new GameController();
        gameView.setModel(gameModel);
        gameView.setController(gameController);
        gameController.setModel(gameModel);
        gameModel.addSubscriber(gameView);

        gameView.initializeBoardChips();
        gameModel.setPlayerStateToOtherPlayerTurn();
;
        gameView.setPreferredSize(new Dimension(600, 600));
        this.add(gameView);
    }

    public void addSocket(Socket fd) {
        socket = fd;
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
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                byte[] buffer = new byte[1024];
                int numBytes;
                String s = "";
                int count = 0;

                while (true) {
//                    Thread.sleep(5000);

                    if (inputStream.available() <= 0) continue;

                    System.out.println("GOT HERE CLIENT GAME 1");
                    numBytes = inputStream.read(buffer);
                    System.out.println("GOT HERE HOST GAME 2");
                    if (numBytes == -1) break;


                    String message = new String(buffer, 0, numBytes);
                    System.out.println(message);
                    System.out.println("Length: "+message.length());
                    if (!message.equals("") && message.length() <= 8) gameModel.takeIncomingMove(message);
                    else if (!message.equals("") && message.length() >= 8) gameModel.takeIncomingMultipleMove(message);

//                    gameModel.setPlayerStateToTheirTurn();
                    gameModel.canJump();
                    System.out.println("set turn to host");

                    while (gameModel.getCurrentState() != GameModel.State.OTHER_PLAYER) {
//                        System.out.println("current state = other player");
//                        count++;
                        Thread.sleep(100);
                    }

                    System.out.println("Message sent to host");

                    s = gameModel.getMessageToSend();
                    outputStream.write(s.getBytes());

                    gameModel.clearMessageToSendString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Broke out of loop, buffer == -1");
        }
    }
}
