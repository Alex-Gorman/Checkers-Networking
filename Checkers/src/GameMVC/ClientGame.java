package GameMVC;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientGame extends JPanel {

    Socket socket;

    GameModel gameModel;
    public ClientGame() {

        /* MVC Setup */
        GameView gameView = new GameView(false);
        gameModel = new GameModel();
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

        gameModel.addSocket(socket);
        gameModel.sendInitMessage(gameModel.clientName);

    }

    public void addSocket(Socket fd) {
        socket = fd;
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
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Broke out of loop, buffer == -1");
        }
    }
}
