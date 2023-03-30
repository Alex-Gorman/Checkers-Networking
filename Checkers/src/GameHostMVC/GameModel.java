package GameHostMVC;

import java.util.*;

public class GameModel {

    enum State {FIRST_PRESS, SECOND_PRESS, OTHER_PLAYER}

//    enum MsgState {MSG_NOT_READY, MSG_NOT_SENT}

    State currentState;

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
        currentState = State.FIRST_PRESS;
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

            if (row > 0 && col > 0) tilesPlayerCanMoveTo.add(tiles[row-1][col-1]);
            if (row > 0 && col < 8) tilesPlayerCanMoveTo.add(tiles[row-1][col+1]);
        }
        System.out.println(tilesPlayerCanMoveTo.size());
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

    public void addTileClick(int row, int col) {

        switch (currentState) {
            case FIRST_PRESS -> {

                /* If the tile is occupied by the other player then don't register anything, don't move anything */
                if (isOccupiedByOtherPlayer(row, col)) return;

                /* If the tile is not occupied at all then don't do anything */
                if (!isOccupied(row, col)) return;

                /* Show to player where the current piece can be moved to */
                canMoveTo(row, col);

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
                System.out.println("validMove="+validMove);


                /* Check if the second button press is a valid move */
                for (GameTile gameTile : tilesPlayerCanMoveTo) {
                    if (gameTile.row == row && gameTile.col == col) {
                        validMove = true;
                    }
                }
                System.out.println("validMove="+validMove);


                /* If valid move then re-assign row and col to piece */
                if (validMove) {

                    createMoveString(row, col);
                    p.row = row;
                    p.col = col;
                    currentState = State.OTHER_PLAYER;
                    System.out.println("changed state");
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

    public State getCurrentState() {
        return currentState;
    }

    public void takeIncomingMove(String incomingMoveMsg) {
        System.out.println("incomingMoveMsg"+incomingMoveMsg);

//        String cleanMessage = incomingMoveMsg.replaceAll("->", "");
//        System.out.println("cleanMessage"+cleanMessage);

        String[] numbers = incomingMoveMsg.split(",");
        System.out.println("numbers array"+ Arrays.toString(numbers));
        int rowPrev = Integer.parseInt(numbers[0]);
        int colPrev = Integer.parseInt(numbers[1]);
        int rowCur = Integer.parseInt(numbers[2]);
        int colCur = Integer.parseInt(numbers[3]);

        for (Piece p: playerTwoPieces) {
            if (p.row == rowPrev && p.col == colPrev) {
                tiles[rowPrev][colPrev].piece = null;
                p.row = rowCur;
                p.col = colCur;
                tiles[rowCur][colCur].piece = p;
                break;
            }
        }
        notifySubscribers();
    }

    public void setPlayerStateToTheirTurn() {
        currentState = State.FIRST_PRESS;
    }
}
