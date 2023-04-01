package GameMVC;

import GameMVC.GameModelSubscriber;
import GameMVC.GameTile;
import GameMVC.Piece;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class GameModel {

    enum State {FIRST_PRESS, SECOND_PRESS, OTHER_PLAYER}

//    enum MsgState {MSG_NOT_READY, MSG_NOT_SENT}

    State currentState;

    static DataOutputStream dout;

    ArrayList<String> msgs = new ArrayList<>();

    Socket socket;

    int numberOfClicks;
    ArrayList<GameModelSubscriber> subs;

    ArrayList<Piece> playerOnePieces;
    ArrayList<Piece> playerTwoPieces;

    GameTile[][] tiles = new GameTile[8][8];

    ArrayList<GameTile> tilesPlayerCanMoveTo = new ArrayList<>();

    /* Variables to store player location for chip being moved */
    int playerRow, playerCol;

    String messageToSend = "";

    public GameModel() {
        synchronized (this) {
            currentState = State.FIRST_PRESS;
        }
//        currentState = State.FIRST_PRESS;
        numberOfClicks = 0;
        subs = new ArrayList<GameModelSubscriber>();
        playerOnePieces = new ArrayList<Piece>();
        playerTwoPieces = new ArrayList<Piece>();
    }

    public void addPiecePlayerOne(Piece p) {
        playerOnePieces.add(p);
        notifySubscribers();
    }

    public void addPiecePlayerTwo(Piece p) {
        playerTwoPieces.add(p);
        notifySubscribers();
    }

    public ArrayList<Piece> getPlayerOnePieces() {
        return playerOnePieces;
    }

    public ArrayList<Piece> getPlayerTwoPieces() {
        return playerTwoPieces;
    }

    public void addSubscriber(GameModelSubscriber newSub) {
        subs.add(newSub);
    }

    private void notifySubscribers() {
        subs.forEach(GameModelSubscriber::modelUpdated);
    }

    public GameTile[][] getTiles() {
        return tiles;
    }

    public ArrayList<GameTile> getTilesPlayerCanMoveTo() {
        return tilesPlayerCanMoveTo;
    }

    public Boolean isOccupied(int row, int col) {
        return (tiles[row][col].piece != null);
    }

    public Boolean isOccupiedByOtherPlayer(int row, int col) {
        return (tiles[row][col].piece != null && tiles[row][col].piece.player == false);
    }



    public void canMoveTo(int row, int col) {
        Piece p = tiles[row][col].piece;

        tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);

        /* If the piece is not a king */
        if (!p.king) {
            if (row > 0 && col > 0 && !isOccupied(row-1, col-1)) tilesPlayerCanMoveTo.add(tiles[row-1][col-1]);
            if (row > 0 && col < 7 && !isOccupied(row-1, col+1)) tilesPlayerCanMoveTo.add(tiles[row-1][col+1]);
        }
        else {

            /* 1 */
            if (row == 0 && col == 7 && !isOccupied(row+1, col-1)) tilesPlayerCanMoveTo.add(tiles[row+1][col-1]);

            /* 2 */
            if (row == 0 && col < 7 && col > 0) {
                if (!isOccupied(row+1, col-1)) tilesPlayerCanMoveTo.add(tiles[row+1][col-1]);
                if (!isOccupied(row+1, col+1)) tilesPlayerCanMoveTo.add(tiles[row+1][col+1]);
            }

            /* 3 */
            if (row == 7 && col == 0 && !isOccupied(row-1, col+1)) tilesPlayerCanMoveTo.add(tiles[row-1][col+1]);

            /* 4 */
            if (row == 7 && col < 7 && col > 0) {
                if (!isOccupied(row-1, col-1)) tilesPlayerCanMoveTo.add(tiles[row-1][col-1]);
                if (!isOccupied(row-1, col+1)) tilesPlayerCanMoveTo.add(tiles[row-1][col+1]);
            }

            /* 5 */
            if (col == 7 && row < 7 && row > 0) {
                if (!isOccupied(row+1, col-1)) tilesPlayerCanMoveTo.add(tiles[row+1][col-1]);
                if (!isOccupied(row-1, col-1)) tilesPlayerCanMoveTo.add(tiles[row-1][col-1]);
            }

            /* 6 */
            if (col == 0 && row < 7 && row > 0) {
                if (!isOccupied(row+1, col+1)) tilesPlayerCanMoveTo.add(tiles[row+1][col+1]);
                if (!isOccupied(row-1, col+1)) tilesPlayerCanMoveTo.add(tiles[row-1][col+1]);
            }

            /* 7 */
            if (col < 7 && col > 0 && row < 7 && row > 0) {
                if (!isOccupied(row+1, col+1)) tilesPlayerCanMoveTo.add(tiles[row+1][col+1]);
                if (!isOccupied(row+1, col-1)) tilesPlayerCanMoveTo.add(tiles[row+1][col-1]);
                if (!isOccupied(row-1, col+1)) tilesPlayerCanMoveTo.add(tiles[row-1][col+1]);
                if (!isOccupied(row-1, col-1)) tilesPlayerCanMoveTo.add(tiles[row-1][col-1]);
            }

        }
    }

    public String createMoveString(int moveToRow, int moveToCol) {
        messageToSend += String.valueOf(7-playerRow);
        messageToSend += ",";
        messageToSend += String.valueOf(7-playerCol);
        messageToSend += ",";
        messageToSend += String.valueOf(7-moveToRow);
        messageToSend += ",";
        messageToSend += String.valueOf(7-moveToCol);
        return messageToSend;
    }

    public void clearMessageToSendString() {
        messageToSend = "";
    }

    public String getMessageToSend() {
        return messageToSend;
    }

    public Boolean canJump() {
        Boolean jumpPossible = false;

        /* Check all player one pieces to see if you can capture player two piece */
        for (Piece p: playerOnePieces) {
            int checkRow = p.row;
            int checkCol = p.col;

            /* Scenario 1 */
            if (checkCol <= 1 && checkRow <= 1) ;

            /* Scenario 2 */
            if (checkCol >= 6 && checkRow <= 1);

            /* Scenario 3 */
            if (checkCol >= 6 && checkRow >= 2) {
                if (isOccupiedByOtherPlayer(checkRow-1, checkCol-1) && !isOccupied(checkRow-2, checkCol-2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol-2]);
                    jumpPossible = true;
                }
            }

            /* Scenario 4 */
            if (checkCol <= 1 && checkRow >= 2) {
                if (isOccupiedByOtherPlayer(checkRow-1, checkCol+1) && !isOccupied(checkRow-2, checkCol+2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol+2]);
                    jumpPossible = true;
                }
            }

            /* Scenario 5 */
            if (checkCol >= 2 && checkCol <= 5 && checkRow >= 2) {
                if (isOccupiedByOtherPlayer(checkRow-1, checkCol+1) && !isOccupied(checkRow-2, checkCol+2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol+2]);
                    jumpPossible = true;
                }
                if (isOccupiedByOtherPlayer(checkRow-1, checkCol-1) && !isOccupied(checkRow-2, checkCol-2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol-2]);
                    jumpPossible = true;
                }
            }

            if (p.isKing()) {

                /* Scenario 6 */
                if (checkRow >= 6) ;

                /* Scenario 7 */
                if (checkRow <= 5 && checkCol <= 1) {
                    if (isOccupiedByOtherPlayer(checkRow+1, checkCol+1) && !isOccupied(checkRow+2, checkCol+2)) {
                        tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol+2]);
                        jumpPossible = true;
                    }
                }

                /* Scenario 8 */
                if (checkRow <= 5 && checkCol >= 6) {
                    if (isOccupiedByOtherPlayer(checkRow+1, checkCol-1) && !isOccupied(checkRow+2, checkCol-2)) {
                        tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol-2]);
                        jumpPossible = true;
                    }
                }

                /* Scenario 9 */
                if (checkRow <= 5 && checkCol <= 5 && checkCol >= 2) {
                    if (isOccupiedByOtherPlayer(checkRow+1, checkCol+1) && !isOccupied(checkRow+2, checkCol+2)) {
                        tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol+2]);
                        jumpPossible = true;
                    }
                    if (isOccupiedByOtherPlayer(checkRow+1, checkCol-1) && !isOccupied(checkRow+2, checkCol-2)) {
                        tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol-2]);
                        jumpPossible = true;
                    }
                }
            }
        }
        return jumpPossible;
    }

    public void addTileClick(int row, int col) {

        switch (currentState) {
            case FIRST_PRESS -> {

                if (canJump()) {
                    currentState = State.SECOND_PRESS;

                    /* Save the location of the piece */
                    playerRow = row;
                    playerCol = col;

                    notifySubscribers();
                    return;
                }

                /* If the tile is occupied by the other player then don't register anything, don't move anything */
                if (isOccupiedByOtherPlayer(row, col)) return;

                /* If the tile is not occupied at all then don't do anything */
                if (!isOccupied(row, col)) return;

                /* Show to player where the current piece can be moved to */
                canMoveTo(row, col);

                if (tilesPlayerCanMoveTo.size() == 0) return;

                /* Save the location of the piece */
                playerRow = row;
                playerCol = col;


                currentState = State.SECOND_PRESS;
                notifySubscribers();
            }
            case SECOND_PRESS -> {

                /* Get the piece of the first button press */
                Piece p = tiles[playerRow][playerCol].piece;


                Boolean validMove = false;
//                System.out.println("validMove="+validMove);


                /* Check if the second button press is a valid move */
                for (GameTile gameTile : tilesPlayerCanMoveTo) {
                    if (gameTile.row == row && gameTile.col == col) {
                        validMove = true;
                    }
                }
                System.out.println("validMove="+validMove);

                if (!validMove) {
                    /* Re-assign these values to be nothing */
                    playerRow = -1;
                    playerCol = -1;

                    /* Remove all tiles player can move to */
                    tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);

                    /* Re-assign state to first press */
                    currentState = State.FIRST_PRESS;

                    notifySubscribers();
                }


                /* If valid move then re-assign row and col to piece */
                if (validMove) {

                    /* Create the string of the move to send to the other player */
                    createMoveString(row, col);

                    /* If the piece reaches the end of the board, then set to king */
                    setIfKing(row, col, p);

                    /* Check if you jumped over a player */
                    int colToDelete = -1;
                    int rowToDelete = -1;
                    if ((Math.abs(row-playerRow))>=2 || (Math.abs(col-playerCol))>=2) {
                        System.out.println("got here math abs");

                        System.out.println("row1 = "+playerRow);
                        System.out.println("col1 = "+playerCol);
                        System.out.println("row2 = "+row);
                        System.out.println("col2 = "+col);


                        if (row < playerRow) {
                            rowToDelete = row + 1;
                            if (col > playerCol) {
                                colToDelete = col - 1;
                            } else {
                                colToDelete = col + 1;
                            }
                        }
                        else if (row > playerRow) {
                            rowToDelete = row - 1;
                            if (col > playerCol) {
                                colToDelete = col - 1;
                            } else {
                                colToDelete = col + 1;
                            }
                        }

                        System.out.println("row to delete"+rowToDelete);
                        System.out.println("col to delete"+colToDelete);



                        /* Remove the player you jumped over */
                        for (Piece pToDelete: playerTwoPieces) {
                            if (pToDelete.row == rowToDelete && pToDelete.col == colToDelete) {
                                System.out.println(pToDelete.row);
                                System.out.println(pToDelete.col);
                                playerTwoPieces.remove(pToDelete);
                                break;
                            }
                        }
                    }


                    /* Update the piece row and col to new values */
                    p.row = row;
                    p.col = col;

                    System.out.println("current state in model 1="+currentState);
                    currentState = State.OTHER_PLAYER;
                    System.out.println("current state in model 2="+currentState);
//                    System.out.println("changed state");
                }

                /* Re-assign these values to be nothing */
                playerRow = -1;
                playerCol = -1;

                /* Remove all tiles player can move to */
                tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);
//                currentState = State.FIRST_PRESS;
                notifySubscribers();
            }
        }
    }

    public void setIfKing(int row, int col, Piece p) {
        if (p.player && row == 0) p.setKing();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void takeIncomingMove(String incomingMoveMsg) {
        String[] numbers = incomingMoveMsg.split(",");
        int rowPrev = Integer.parseInt(numbers[0]);
        int colPrev = Integer.parseInt(numbers[1]);
        int rowCur = Integer.parseInt(numbers[2]);
        int colCur = Integer.parseInt(numbers[3]);

        System.out.println("got here into takeIncomingMove");

        for (Piece p: playerTwoPieces) {
            System.out.println(p.row);
            System.out.println(p.col);
        }

        for (Piece p: playerTwoPieces) {
            if (p.row == rowPrev && p.col == colPrev) {
                System.out.println("found piece in takeIncomingMove");
                tiles[rowPrev][colPrev].piece = null;
                p.row = rowCur;
                p.col = colCur;
                tiles[rowCur][colCur].piece = p;


                /* Check if the piece made a jump */
                int colToDelete = -1;
                int rowToDelete = -1;
                if ((Math.abs(rowPrev - rowCur))>=2 || (Math.abs(colPrev - colCur))>=2) {
                    if (!p.isKing()) {
                        if (rowCur > rowPrev) {
                            rowToDelete = rowCur - 1;
                            if (colCur > colPrev) colToDelete = colCur - 1;
                            else colToDelete = colCur + 1;

                            System.out.println("row to delete = "+rowToDelete);
                            System.out.println("col to delete = "+colToDelete);

                            /* Remove the player you jumped over */
                            for (Piece pToDelete: playerOnePieces) {
                                if (pToDelete.row == rowToDelete && pToDelete.col == colToDelete) {
                                    System.out.println(pToDelete.row);
                                    System.out.println(pToDelete.col);
                                    playerOnePieces.remove(pToDelete);
                                    break;
                                }
                            }
                        }
                    } else {
                        if (rowCur > rowPrev) {
                            rowToDelete = rowCur + 1;
                            if (colCur > colPrev) colToDelete = colCur - 1;
                            else colToDelete = colCur + 1;

                            System.out.println("row to delete = "+rowToDelete);
                            System.out.println("col to delete = "+colToDelete);

                            /* Remove the player you jumped over */
                            for (Piece pToDelete: playerOnePieces) {
                                if (pToDelete.row == rowToDelete && pToDelete.col == colToDelete) {
                                    System.out.println(pToDelete.row);
                                    System.out.println(pToDelete.col);
                                    playerOnePieces.remove(pToDelete);
                                    break;
                                }
                            }
                        } else if (rowPrev > rowCur) {
                            rowToDelete = rowCur + 1;
                            if (colCur > colPrev) colToDelete = colCur - 1;
                            else colToDelete = colCur + 1;

                            /* Remove the player you jumped over */
                            for (Piece pToDelete: playerOnePieces) {
                                if (pToDelete.row == rowToDelete && pToDelete.col == colToDelete) {
                                    System.out.println(pToDelete.row);
                                    System.out.println(pToDelete.col);
                                    playerOnePieces.remove(pToDelete);
                                    break;
                                }
                            }
                        }
                    }
                }

                /* If the piece makes it to the last row, then set it to a king */
                if (p.row == 7) {
                    p.setKing();
                    System.out.println("king me");
                }
                break;
            }
        }
        notifySubscribers();
    }

    public void setPlayerStateToTheirTurn() {
        currentState = State.FIRST_PRESS;
    }

    public void setPlayerStateToOtherPlayerTurn() {
        currentState = State.OTHER_PLAYER;
    }

    public void addSocket(Socket socket){
        this.socket = socket;
        try {
            dout = new DataOutputStream(socket.getOutputStream());
        }catch (Exception e){

        }
    }
    public void sendMessage(String msg){
        String out = msg;
        try {
            dout.writeUTF(out);

        }catch (Exception e){}

        notifySubscribers();
    }
}
