package GameMVC;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HostGame extends JPanel {

    Socket clientSocket;

    ServerSocket serverSocket;

    GameModel gameModel;

    public HostGame(Boolean host) {

        /* MVC Setup */
        GameView gameView = new GameView(host);
        gameModel = new GameModel(true);
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

        JButton quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        quitButton.setMaximumSize(new Dimension(80,30));
        quitButton.addActionListener(e -> {
            gameController.quitGame();

        });
        setBackground(new Color(159,235,237,160));

        JPanel quitPanel = new JPanel();
        quitPanel.add(quitButton);

        quitPanel.setBackground(new Color(159,235,237,160));

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
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        this.add(quitPanel,gbc);

    }

    public void sendInitMsg (){
        gameModel.sendInitMessage(gameModel.hostName);
    }


    public void addClientSocket(Socket fd) {
        clientSocket = fd;
        gameModel.addSocket(clientSocket);

    }

    public void addServerSocket(ServerSocket fd) {
        serverSocket = fd;
        gameModel.setServerSocket(serverSocket);
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
                                }else if (msg.equals("%")){
                                    clientSocket.close();
                                    serverSocket.close();
                                    gameModel.toMainMenu();
                                }else {
                                    if (msg.length() <= 8) gameModel.takeIncomingMove(msg);
                                    else gameModel.takeIncomingMultipleMove(msg);
                                    gameModel.canJump();
                                }

                        }
                    }
                } catch (IOException e) {
                    gameModel.toMainMenu();
                }
        }
    }

    public void setMainMenu(MainMenu mainMenu){
        gameModel.setMainMenu(mainMenu);
    }

    public void setFrame(JFrame frame){
        gameModel.setFrame(frame);
    }

}
