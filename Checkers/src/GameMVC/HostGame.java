package GameMVC;

import javax.swing.*;
import java.awt.*;
import java.io.*;
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
        ChatView chatView = new ChatView(host);
        chatView.setModel(gameModel);
        chatView.setController(gameController);
        gameModel.addSubscriber(chatView);
        chatView.setPreferredSize(new Dimension(600, 200));

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7,7,7,7);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(gameView,gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(chatView,gbc);

        gameModel.addSocket(clientSocket);
        gameModel.sendInitMessage(gameModel.hostName);
    }

    public void addClientSocket(Socket fd) {
        clientSocket = fd;
    }

    public void setHostUsername(String hostUsername){ gameModel.setHostName(hostUsername);}

    public void startMessaging() {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }

    public class MyRunnable implements Runnable {

        @Override
        public void run() {
                try {
                    while(true) {
                        DataInputStream din = new DataInputStream(clientSocket.getInputStream());
                        gameModel.setDataOutStream(new DataOutputStream(clientSocket.getOutputStream()));

                        while(true) {
                            String msg = din.readUTF();
                            if (msg.charAt(0) == '*'){
                                gameModel.receiveChatMessage(msg);
                            }else if (msg.charAt(0) == '@'){
                                gameModel.receiveInitMessage(msg,true);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
