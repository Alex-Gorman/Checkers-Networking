package GameClientMVC;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientGameModel {

    enum State {FIRST_PRESS, SECOND_PRESS, OTHER_PLAYER}

    State currentState;

    int numberOfClicks;
    ArrayList<ClientGameModelSubscriber> subs;

    ArrayList<ClientPiece> playerOnePieces;
    ArrayList<ClientPiece> playerTwoPieces;

    ClientGameTile[][] tiles = new ClientGameTile[8][8];

    ArrayList<ClientGameTile> tilesPlayerCanMoveTo = new ArrayList<>();

    /* Variables to store player location for chip being moved */
    int playerRow, playerCol;

    String messageToSend = "";

    public ClientGameModel() {
        currentState = State.OTHER_PLAYER;
        numberOfClicks = 0;
        subs = new ArrayList<ClientGameModelSubscriber>();
        playerOnePieces = new ArrayList<ClientPiece>();
        playerTwoPieces = new ArrayList<ClientPiece>();
    }

    public void addPiecePlayerOne(ClientPiece p) {
        playerOnePieces.add(p);
        notifySubscribers();
    }

    public void addPiecePlayerTwo(ClientPiece p) {
        playerTwoPieces.add(p);
        notifySubscribers();
    }

    public ArrayList<ClientPiece> getPlayerOnePieces() {
        return playerOnePieces;
    }

    public ArrayList<ClientPiece> getPlayerTwoPieces() {
        return playerTwoPieces;
    }

    public void addSubscriber(ClientGameModelSubscriber newSub) {
        subs.add(newSub);
    }

    private void notifySubscribers() {
        subs.forEach(ClientGameModelSubscriber::modelUpdated);
    }

    public ClientGameTile[][] getTiles() {
        return tiles;
    }

    public ArrayList<ClientGameTile> getTilesPlayerCanMoveTo() {
        return tilesPlayerCanMoveTo;
    }

    public Boolean isOccupied(int row, int col) {
        return (tiles[row][col].piece != null);
    }

    public Boolean isOccupiedByOtherPlayer(int row, int col) {
        return (tiles[row][col].piece != null && tiles[row][col].piece.player == false);
    }

    public void canMoveTo(int row, int col) {
        ClientPiece p = tiles[row][col].piece;

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
                ClientPiece p = tiles[playerRow][playerCol].piece;


                Boolean validMove = false;

                /* Check if the second button press is a valid move */
                for (ClientGameTile gameTile : tilesPlayerCanMoveTo) {
                    if (gameTile.row == row && gameTile.col == col) {
                        validMove = true;
                    }
                }

                /* If valid move then re-assign row and col to piece */
                if (validMove) {
                    p.row = row;
                    p.col = col;
                    currentState = State.OTHER_PLAYER;
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

    public void takeIncomingMove(String incomingMoveMsg) {
        System.out.println("incomingMoveMsg"+incomingMoveMsg);

        String cleanMessage = incomingMoveMsg.replaceAll("->", "");
        System.out.println("cleanMessage"+cleanMessage);

        String[] numbers = cleanMessage.split(",");
        System.out.println("numbers array"+ Arrays.toString(numbers));
        int rowPrev = Integer.parseInt(numbers[0]);
        int colPrev = Integer.parseInt(numbers[1]);
        int rowCur = Integer.parseInt(numbers[2]);
        int colCur = Integer.parseInt(numbers[3]);

        for (ClientPiece p: playerTwoPieces) {
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

    public ClientGameModel.State getCurrentState() {
        return currentState;
    }
}
