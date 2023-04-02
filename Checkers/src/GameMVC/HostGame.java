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
        chatView.setPreferredSize(new Dimension(300, 500));

        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setModel(gameModel);
        scoreBoard.setController(gameController);
        gameModel.addSubscriber(scoreBoard);
        scoreBoard.setPreferredSize(new Dimension(300, 100));

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7,7,7,7);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(gameView,gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        this.add(chatView,gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        this.add(scoreBoard,gbc);



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

                        byte[] buffer = new byte[1024];
                        int numBytes;
                        String s = "";

                        while(true) {
                            String msg = din.readUTF();
                            if (msg.charAt(0) == '*'){
                                gameModel.receiveChatMessage(msg);
                            }else if (msg.charAt(0) == '@'){
                                gameModel.receiveInitMessage(msg,true);
                            }else {
                                gameModel.takeIncomingMove(msg);
                                gameModel.canJump();


                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
