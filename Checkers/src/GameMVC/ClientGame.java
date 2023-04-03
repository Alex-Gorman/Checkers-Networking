package GameMVC;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

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

        ChatView chatView = new ChatView(false);
        chatView.setModel(gameModel);
        chatView.setController(gameController);
        gameModel.addSubscriber(chatView);
        chatView.setPreferredSize(new Dimension(300, 500));

        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setModel(gameModel);
        scoreBoard.setController(gameController);
        gameModel.addSubscriber(scoreBoard);
        scoreBoard.setPreferredSize(new Dimension(300, 100));
        this.setBackground(new Color(159,235,237,160)); // Set the background color to red


        JButton quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        quitButton.setMaximumSize(new Dimension(80,30));
        quitButton.addActionListener(e -> {
            gameController.quitGame();
        });


        JPanel quitPanel = new JPanel();
        quitPanel.add(quitButton);
        quitPanel.setBackground(new Color(159,235,237,0)); // Set the background color to red

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7,7,7,7);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
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
        gameModel.sendInitMessage(gameModel.clientName);
    }

    public void addSocket(Socket fd) {
        socket = fd;
        gameModel.addSocket(socket);
    }

    public void setClientUsername(String clientUsername){ gameModel.setClientName(clientUsername);}

    public void startMessaging() {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }

    public class MyRunnable implements Runnable {

        @Override
        public void run() {
            try {
                DataInputStream din = new DataInputStream(socket.getInputStream());
                gameModel.setDataOutStream(new DataOutputStream(socket.getOutputStream()));



                while(true) {

                        String msg = din.readUTF();
                        if (msg.charAt(0) == '*'){
                            gameModel.receiveChatMessage(msg);
                        }else if(msg.charAt(0) == '@'){
                            gameModel.receiveInitMessage(msg,false);
                        }else if (msg.equals("%")){
                            socket.close();
                            gameModel.toMainMenu();
                        }
                        else{

                            if (msg.length() <= 8) gameModel.takeIncomingMove(msg);
                            else gameModel.takeIncomingMultipleMove(msg);

                            gameModel.canJump();

                        }

                }
            } catch (IOException e) {
                gameModel.toMainMenu();
            }

            System.out.println("Broke out of loop, buffer == -1");
        }
    }

    public void setMainMenu(MainMenu mainMenu){
        gameModel.setMainMenu(mainMenu);
    }

    public void setFrame(JFrame frame){
        gameModel.setFrame(frame);
    }

//    public void setMySocket(Socket socket){
//        gameModel.setMySocket(socket);
//    }

}
