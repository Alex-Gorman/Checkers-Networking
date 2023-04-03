package GameMVC;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameModel {

    enum State {FIRST_PRESS, SECOND_PRESS, OTHER_PLAYER, CAN_JUMP, CAN_JUMP_AGAIN}

    State currentState;

    int numberOfClicks;
    ArrayList<GameModelSubscriber> subs;

    ArrayList<Piece> playerOnePieces;
    ArrayList<Piece> playerTwoPieces;

    GameTile[][] tiles = new GameTile[8][8];

    ArrayList<GameTile> tilesPlayerCanMoveTo = new ArrayList<>();

    ArrayList<GameTile> tilesOfPiecesThatCanJump = new ArrayList<>();

    /* Variables to store player location for chip being moved */
    int playerRow, playerCol;

    String messageToSend = "";

    Socket socket;

    ServerSocket serverSocket;
    ArrayList<String> chatMessage = new ArrayList<>();
    static DataOutputStream dout;

    String hostName = "Player 1";
    String clientName = "Player 2";
    String chatPrefix = "*";
    String initPrefix = "@";

    int hostScore = 0;
    int clientScore = 0;

    MainMenu mainMenu;
    JFrame frame;

    Boolean host;

    Boolean gameStarted;

    String turn;
    public GameModel(Boolean host) {
        this.host = host;

//        gameStarted = true;

        synchronized (this) {
            currentState = State.FIRST_PRESS;
        }
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

    /* Get all the tiles of the game board */
    public GameTile[][] getTiles() {
        return tiles;
    }

    /* Get the tiles a player can move to */
    public ArrayList<GameTile> getTilesPlayerCanMoveTo() {
        return tilesPlayerCanMoveTo;
    }

    /* Check to see if given row and col of board are occupied by any player */
    public Boolean isOccupied(int row, int col) {
        return (tiles[row][col].piece != null);
    }

    /* Check to see if given row and col of board are occupied by another player */
    public Boolean isOccupiedByOtherPlayer(int row, int col) {
        return (tiles[row][col].piece != null && tiles[row][col].piece.player == false);
    }


    /* Update the array of the tiles that the player can move to */
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

    /* Create the string of moves to send to the other player */
    public String createMoveString(int moveToRow, int moveToCol, Boolean append) {
        if (!append) {
            messageToSend += String.valueOf(7-playerRow);
            messageToSend += ",";
            messageToSend += String.valueOf(7-playerCol);
            messageToSend += ",";
            messageToSend += String.valueOf(7-moveToRow);
            messageToSend += ",";
            messageToSend += String.valueOf(7-moveToCol);
            return messageToSend;
        } else {
            messageToSend += "+";
            messageToSend += String.valueOf(7-playerRow);
            messageToSend += ",";
            messageToSend += String.valueOf(7-playerCol);
            messageToSend += ",";
            messageToSend += String.valueOf(7-moveToRow);
            messageToSend += ",";
            messageToSend += String.valueOf(7-moveToCol);
            return messageToSend;
        }
    }

    public void clearMessageToSendString() {
        messageToSend = "";
    }

    public String getMessageToSend() {
        return messageToSend;
    }

    /* Return true if there are tiles the player can jump to (jump over other player piece) */
    public Boolean canJump() {
        tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);
        Boolean jumpPossible = false;
        tilesOfPiecesThatCanJump.removeAll(tilesOfPiecesThatCanJump);

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
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                    jumpPossible = true;
                }
            }

            /* Scenario 4 */
            if (checkCol <= 1 && checkRow >= 2) {
                if (isOccupiedByOtherPlayer(checkRow-1, checkCol+1) && !isOccupied(checkRow-2, checkCol+2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol+2]);
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                    jumpPossible = true;
                }
            }

            /* Scenario 5 */
            if (checkCol >= 2 && checkCol <= 5 && checkRow >= 2) {
                if (isOccupiedByOtherPlayer(checkRow-1, checkCol+1) && !isOccupied(checkRow-2, checkCol+2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol+2]);
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                    jumpPossible = true;
                }
                if (isOccupiedByOtherPlayer(checkRow-1, checkCol-1) && !isOccupied(checkRow-2, checkCol-2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol-2]);
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
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
                        tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                        jumpPossible = true;
                    }
                }

                /* Scenario 8 */
                if (checkRow <= 5 && checkCol >= 6) {
                    if (isOccupiedByOtherPlayer(checkRow+1, checkCol-1) && !isOccupied(checkRow+2, checkCol-2)) {
                        tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol-2]);
                        tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                        jumpPossible = true;
                    }
                }

                /* Scenario 9 */
                if (checkRow <= 5 && checkCol <= 5 && checkCol >= 2) {
                    if (isOccupiedByOtherPlayer(checkRow+1, checkCol+1) && !isOccupied(checkRow+2, checkCol+2)) {
                        tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol+2]);
                        tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                        jumpPossible = true;
                    }
                    if (isOccupiedByOtherPlayer(checkRow+1, checkCol-1) && !isOccupied(checkRow+2, checkCol-2)) {
                        tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol-2]);
                        tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                        jumpPossible = true;
                    }
                }
            }
        }
        notifySubscribers();
        if (jumpPossible) currentState = State.CAN_JUMP;
        else currentState = State.FIRST_PRESS;
        return jumpPossible;
    }

    /* Return true if there are tiles the player can jump to (jump over other player piece) */
    public Boolean canJumpAgain(Piece p) {
        tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);
        Boolean jumpPossible = false;
        tilesOfPiecesThatCanJump.removeAll(tilesOfPiecesThatCanJump);

        /* Check all player one pieces to see if you can capture player two piece */
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
                tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                jumpPossible = true;
            }
        }

        /* Scenario 4 */
        if (checkCol <= 1 && checkRow >= 2) {
            if (isOccupiedByOtherPlayer(checkRow-1, checkCol+1) && !isOccupied(checkRow-2, checkCol+2)) {
                tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol+2]);
                tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                jumpPossible = true;
            }
        }

        /* Scenario 5 */
        if (checkCol >= 2 && checkCol <= 5 && checkRow >= 2) {
            if (isOccupiedByOtherPlayer(checkRow-1, checkCol+1) && !isOccupied(checkRow-2, checkCol+2)) {
                tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol+2]);
                tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                jumpPossible = true;
            }
            if (isOccupiedByOtherPlayer(checkRow-1, checkCol-1) && !isOccupied(checkRow-2, checkCol-2)) {
                tilesPlayerCanMoveTo.add(tiles[checkRow-2][checkCol-2]);
                tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
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
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                    jumpPossible = true;
                }
            }

            /* Scenario 8 */
            if (checkRow <= 5 && checkCol >= 6) {
                if (isOccupiedByOtherPlayer(checkRow+1, checkCol-1) && !isOccupied(checkRow+2, checkCol-2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol-2]);
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                    jumpPossible = true;
                }
            }

            /* Scenario 9 */
            if (checkRow <= 5 && checkCol <= 5 && checkCol >= 2) {
                if (isOccupiedByOtherPlayer(checkRow+1, checkCol+1) && !isOccupied(checkRow+2, checkCol+2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol+2]);
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                    jumpPossible = true;
                }
                if (isOccupiedByOtherPlayer(checkRow+1, checkCol-1) && !isOccupied(checkRow+2, checkCol-2)) {
                    tilesPlayerCanMoveTo.add(tiles[checkRow+2][checkCol-2]);
                    tilesOfPiecesThatCanJump.add(tiles[checkRow][checkCol]);
                    jumpPossible = true;
                }
            }
        }
        notifySubscribers();
        if (jumpPossible) currentState = State.CAN_JUMP_AGAIN;
        else {
            tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);
            tilesOfPiecesThatCanJump.removeAll(tilesOfPiecesThatCanJump);
        }
        return jumpPossible;
    }

    public void resetTilesPlayerCanMoveTo() {
        tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);
    }

    public void resetTilesOfPiecesThatCanJump() {
        tilesOfPiecesThatCanJump.removeAll(tilesOfPiecesThatCanJump);
    }

    public void removePieceFromPlayer2(int row, int col) {
        for (Piece pToDelete: playerTwoPieces) {
            if (pToDelete.row == row && pToDelete.col == col) {
                playerTwoPieces.remove(pToDelete);
                break;
            }
        }
    }

    public void removePieceFromPlayer1(int row, int col) {
        for (Piece pToDelete: playerOnePieces) {
            if (pToDelete.row == row && pToDelete.col == col) {
                playerOnePieces.remove(pToDelete);
                break;
            }
        }
    }

    public void addTileClick(int row, int col) {

        switch (currentState) {
            case CAN_JUMP_AGAIN -> {
                Piece p = null;

                for (GameTile tile: tilesOfPiecesThatCanJump) {
                    if (tile.piece.row == playerRow && tile.piece.col == playerCol) {
                        p = tile.piece;
                        break;
                    }
                }

                if (p == null) return;

                Boolean validMove = false;
                for (GameTile t: tilesPlayerCanMoveTo) {
                    if (t.col == col && t.row == row) {
                        validMove = true;
                        break;
                    }
                }

                if (!validMove) return;

                /* Check if you jumped over a player */
                int colToDelete = -1;
                int rowToDelete = -1;

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
                /* Remove the player you jumped over */
                removePieceFromPlayer2(rowToDelete, colToDelete);

                p.row = row;
                p.col = col;

                createMoveString(row, col, true);

                if (p.row == 0) p.setKing();

                if (canJumpAgain(p)) {
                    playerRow = row;
                    playerCol = col;
                } else {
                    playerRow = -1;
                    playerCol = -1;
//                    currentState = State.OTHER_PLAYER;
                    setPlayerStateToOtherPlayerTurn();
                }

                notifySubscribers();
            }


            case CAN_JUMP -> {
                Piece p = null;
//                notifySubscribers();

                for (GameTile tile: tilesOfPiecesThatCanJump) {
                    if (tile.piece.row == row && tile.piece.col == col) {
                        p = tile.piece;
                        break;
                    }
                }

                if (p == null) return;
                else {
                    playerRow = p.row;
                    playerCol = p.col;
                    currentState = State.SECOND_PRESS;
                }

            }

            case FIRST_PRESS -> {

                /* If the tile is occupied by the other player then don't register anything, don't move anything */
                if (isOccupiedByOtherPlayer(row, col)) return;

                /* If the tile is not occupied at all then don't do anything */
                if (!isOccupied(row, col)) return;

                /* Show to player where the current piece can be moved to */
                canMoveTo(row, col);

                /* If no tiles player can move to then just return and try again */
                if (tilesPlayerCanMoveTo.size() == 0) return;

                /* Save the location of the piece */
                playerRow = row;
                playerCol = col;


                currentState = State.SECOND_PRESS;
                notifySubscribers();
            }
            case SECOND_PRESS -> {

                System.out.println("SECOND_PRESS");

                /* Get the piece of the first button press */
                Piece p = tiles[playerRow][playerCol].piece;



                Boolean validMove = false;

                /* Check if the second button press is a valid move */
                for (GameTile gameTile : tilesPlayerCanMoveTo) {
                    if (gameTile.row == row && gameTile.col == col) {
                        validMove = true;
                    }
                }

                if (!validMove) {
                    System.out.println("Invalid move");

                    /* Re-assign these values to be nothing */
                    playerRow = -1;
                    playerCol = -1;

                    /* Remove all tiles player can move to */
                    resetTilesPlayerCanMoveTo();

                    /* Re-assign state */
                    System.out.println("Size of tiles pieces that can jump array = "+tilesOfPiecesThatCanJump.size());
                    if (tilesOfPiecesThatCanJump.size() != 0) {
                        canJump();
                        return ;
                    }
                    else  {
                        currentState = State.FIRST_PRESS;
                        /* Update game board */
                        notifySubscribers();
                    }

//                    /* Update game board */
//                    notifySubscribers();
                }


                /* If valid move then re-assign row and col to piece */
                if (validMove) {

                    Boolean justMadeAJump = false;

                    resetTilesOfPiecesThatCanJump();

                    /* Create the string of the move to send to the other player */
                    createMoveString(row, col, false);

                    /* If the piece reaches the end of the board, then set to king */
                    setIfKing(row, col, p);

                    /* Check if you jumped over a player */
                    int colToDelete = -1;
                    int rowToDelete = -1;
                    if ((Math.abs(row-playerRow))>=2 || (Math.abs(col-playerCol))>=2) {
                        justMadeAJump = true;

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
                        /* Remove the player you jumped over */
                        removePieceFromPlayer2(rowToDelete, colToDelete);
                    }

                    /* Update the piece row and col to new values */
                    p.row = row;
                    p.col = col;

                    if (justMadeAJump && canJumpAgain(p)) {
                        playerRow = row;
                        playerCol = col;
                        System.out.println("playerRow="+playerRow);
                        System.out.println("playerCol="+playerCol);
                        notifySubscribers();

                        return;
                    } else {
//                        currentState = State.OTHER_PLAYER;
                        setPlayerStateToOtherPlayerTurn();
                    }

                }

                /* Re-assign these values to be nothing */
                playerRow = -1;
                playerCol = -1;

                /* Remove all tiles player can move to */
                tilesPlayerCanMoveTo.removeAll(tilesPlayerCanMoveTo);
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

        int count = 0;
        for (Piece p: playerOnePieces) {
            System.out.println(" ");
            System.out.println("count="+count);
            System.out.println("row="+p.row);
            System.out.println("col="+p.col);
            System.out.println(" ");
            count++;
        }

        for (Piece p: playerTwoPieces) {
            if (p.row == rowPrev && p.col == colPrev) {
                tiles[rowPrev][colPrev].piece = null;
                p.row = rowCur;
                p.col = colCur;
                tiles[rowCur][colCur].piece = p;

                System.out.println(" ");
                System.out.println("ROW PRV ="+rowPrev);
                System.out.println("COL PRV ="+colPrev);
                System.out.println("ROW CUR ="+rowCur);
                System.out.println("COL CUR ="+colCur);


                /* Check if the piece made a jump */
                int colToDelete = -1;
                int rowToDelete = -1;
                if ((Math.abs(rowPrev - rowCur))>=2 || (Math.abs(colPrev - colCur))>=2) {
                    System.out.println("JUMP SHOULD BE MADE");
                    if (rowCur > rowPrev) {
                        rowToDelete = rowCur - 1;
                        if (colCur > colPrev) colToDelete = colCur - 1;
                        else colToDelete = colCur + 1;

                        /* Remove the player you jumped over */
                        removePieceFromPlayer1(rowToDelete, colToDelete);
                        notifySubscribers();

                    }

                    if (p.isKing()) {
                        if (rowPrev > rowCur) {
                            rowToDelete = rowCur + 1;
                            if (colCur > colPrev) colToDelete = colCur - 1;
                            else colToDelete = colCur + 1;

                            /* Remove the player you jumped over */
                            removePieceFromPlayer1(rowToDelete, colToDelete);
                            notifySubscribers();
                        }
                    }
                }

                System.out.println("ROW TO DELETE ="+rowToDelete);
                System.out.println("COL TO DELETE ="+colToDelete);

                /* If the piece makes it to the last row, then set it to a king */
                if (p.row == 7) {
                    p.setKing();
                }
                break;
            }
        }
        if (checkPlayerOneLost()) {
            System.out.println("got here");
            updateScore(hostScore, clientScore+1);

            if (host) {
                initializeHostGameBoard();
            } else {
                initializeClientGameBoard();
            }


            currentState = State.OTHER_PLAYER;
            messageToSend = "";
            System.out.println("CURRENT STATE ="+currentState);

        }

        notifySubscribers();
    }

    public void takeIncomingMultipleMove(String message) {
        String[] splitStrings = message.split("\\+");

        for (String s: splitStrings) {
            takeIncomingMove(s);
        }
    }

    public void setPlayerStateToTheirTurn() {
        currentState = State.FIRST_PRESS;
    }


    public Boolean checkGameOver() {
        return (checkPlayerOneLost() || checkPlayerTwoLost());
    }

    public Boolean checkPlayerOneLost() {
        return (playerOnePieces.size()==0);
    }

    public Boolean checkPlayerTwoLost() {
        return (playerTwoPieces.size()==0);
    }

    public void resetGameVariables() {
        playerOnePieces.removeAll(playerOnePieces);
        playerTwoPieces.removeAll(playerTwoPieces);
        playerRow = -1;
        playerCol = -1;
//        messageToSend = "";
    }

    public void initializeHostGameBoard() {
        resetGameVariables();
        currentState = State.FIRST_PRESS;

        /* Set the black player (2) moves */
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    this.addPiecePlayerTwo(new Piece(row, col, false, Color.BLUE));
                }
            }
        }

        /* Set the red player (1) moves */
        for (int row = 5; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    this.addPiecePlayerOne(new Piece(row, col, true, Color.RED));
                }
            }
        }
        notifySubscribers();
    }

    public void initializeClientGameBoard() {
        resetGameVariables();
        currentState = State.OTHER_PLAYER;

//        setPlayerStateToOtherPlayerTurn();

        /* Set the black player (2) moves */
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    this.addPiecePlayerTwo(new Piece(row, col, false, Color.RED));
                }
            }
        }

        /* Set the red player (1) moves */
        for (int row = 5; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0) {
                    this.addPiecePlayerOne(new Piece(row, col, true, Color.BLUE));
                }
            }
        }
        notifySubscribers();
    }

    public void setPlayerStateToOtherPlayerTurn() {

        if (checkGameOver()) {
            if (checkPlayerTwoLost()) {
                updateScore(hostScore+1, clientScore);
            } else if (checkPlayerTwoLost()) {
                updateScore(hostScore, clientScore+1);
            }

            if (host) {
                initializeHostGameBoard();
            } else {
                initializeClientGameBoard();
            }

            currentState = State.FIRST_PRESS;
        }

        else currentState = State.OTHER_PLAYER;
        try {
            dout.writeUTF( getMessageToSend());
            clearMessageToSendString();
        }catch (Exception e){

        }

    }

    // Leo
    public void addSocket(Socket socket){
        this.socket = socket;
        try {
            dout = new DataOutputStream(socket.getOutputStream());
        }catch (Exception e){

        }
    }

    public void sendChatMessage(String msg, boolean isHost){
        try {
            if (isHost){
                this.chatMessage.add(hostName + ": " + msg);
                dout.writeUTF(chatPrefix + hostName + ": " + msg);
            }
            else{
                this.chatMessage.add(clientName + ": " + msg);
                dout.writeUTF(chatPrefix + clientName + ": " + msg);
            }
        }catch (Exception e){

        }
        notifySubscribers();
    }

    public void sendInitMessage(String msg){
        try {
            dout.writeUTF(initPrefix + msg);
        }catch (Exception e){

        }
    }

    public void receiveChatMessage(String msg){
        msg = msg.substring(1);
        this.chatMessage.add(msg);
        notifySubscribers();
    }

    public void receiveInitMessage(String msg, boolean isHost){
        msg = msg.substring(1);
        if (isHost){
            setClientName(msg);
        }else{
            setHostName(msg);
        }

    }

    public ArrayList<String> getChatMessage(){
        return this.chatMessage;
    }

    public void setDataOutStream(DataOutputStream dout){
        this.dout = dout;
    }

    public void setHostName(String hostName){
        this.hostName = hostName;
    }

    public void setClientName(String clientName){
        this.clientName = clientName;
    }

    public void setMainMenu(MainMenu mainMenu) {this.mainMenu = mainMenu;}

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void toMainMenu(){
        try {
            dout.writeUTF("%");
        }catch (Exception e){
        }
        try {
            serverSocket.close();
        }catch (Exception e){
        }
        try {
            socket.close();
        }catch (Exception e){
        }
        this.frame.getContentPane().removeAll();
        this.frame.getContentPane().add(this.mainMenu);
        this.frame.revalidate();
        this.frame.repaint();
    }

    // Alex, please use this function to update score board
    public void updateScore(int newHostScore, int newClientScore){
        this.hostScore = newHostScore;
        this.clientScore = newClientScore;
        notifySubscribers();
    }

    // Alex, use this to update turn, the parameter is the username
    public void setTurn(String username) {
        this.turn = turn;
        notifySubscribers();
    }

    // Alex, when game over we need to start a new round
    public void restart() {

    }
}
