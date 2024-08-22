package logic;

import gui.GameFrame;
import model.*;

import static util.Constants.*;
import static util.Util.*;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Game implements GameBoard {

    Thread RequestListener;
    Side Turn;
    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    GameFrame gameFrame;
    Side side;

    public Game(Side initialTurn, Socket socket,ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream, Side side, GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.Turn = initialTurn;
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
        this.side = side;

    }

    @Override
    public void initGameBoardPieces() {
        // initialize white pieces
        GameTiles[0][0] = new Archer(0, 0, Side.WHITE);
        GameTiles[0][1] = new Archer(1, 0, Side.WHITE);
        GameTiles[0][2] = new Knight(2, 0, Side.WHITE);
        GameTiles[0][3] = new Castle(3, 0, Side.WHITE);
        GameTiles[0][4] = new Assassin(4, 0, Side.WHITE);
        GameTiles[0][5] = new Knight(5, 0, Side.WHITE);
        GameTiles[0][6] = new Archer(6, 0, Side.WHITE);
        GameTiles[0][7] = new Archer(7, 0, Side.WHITE);
        for (int i = 0; i < GameTiles[1].length; i++)
            GameTiles[1][i] = new Soldier(i, 1, Side.WHITE);

        // initialize black pieces
        GameTiles[7][0] = new Archer(0, 7, Side.BLACK);
        GameTiles[7][1] = new Archer(1, 7, Side.BLACK);
        GameTiles[7][2] = new Knight(2, 7, Side.BLACK);
        GameTiles[7][3] = new Assassin(3, 7, Side.BLACK);
        GameTiles[7][4] = new Castle(4, 7, Side.BLACK);
        GameTiles[7][5] = new Knight(5, 7, Side.BLACK);
        GameTiles[7][6] = new Archer(6, 7, Side.BLACK);
        GameTiles[7][7] = new Archer(7, 7, Side.BLACK);
        for (int i = 0; i < GameTiles[6].length; i++)
            GameTiles[6][i] = new Soldier(i, 6, Side.BLACK);

    }

    @Override
    public void movePiece(int row, int col, int destRow, int destCol) {
        int TILE_WIDTH = TILE_DIMENSION.width;
        int TILE_HEIGHT = TILE_DIMENSION.height;
        Piece piece = ((Piece) GameTiles[row][col]);
        JLabel pieceLabel = piece.getPieceLabel();
        pieceLabel.setBounds(destCol * TILE_WIDTH, destRow * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        pieceLabel.removeAll();
        Set<Piece> adjacentPieces = null;
        if (piece instanceof canIncreaseOrDecreaseAdjacentPiecesPower) {
            adjacentPieces = new HashSet<>();
            for (int i = 0; i < ADJACENT_DIRECTIONS.length; i++) {
                int dirX = ADJACENT_DIRECTIONS[i][0];
                int dirY = ADJACENT_DIRECTIONS[i][1];
                try {
                    if (GameTiles[row + dirY][col + dirX] != null && GameTiles[row + dirY][col + dirX] instanceof Piece adjacentPiece) {
                        adjacentPieces.add(adjacentPiece);
                    }
                    if (GameTiles[destRow + dirY][destCol + dirX] != null && GameTiles[destRow + dirY][destCol + dirX] instanceof Piece adjacentPiece) {
                        if (!adjacentPiece.equals(piece))
                            adjacentPieces.add(adjacentPiece);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // ignored
                }
            }
        }

        piece.setX(destCol);
        piece.setY(destRow);
        GameTiles[destRow][destCol] = piece;
        GameTiles[row][col] = null;
        if (adjacentPieces != null && !adjacentPieces.isEmpty()) {
            for (Piece adjP : adjacentPieces) {
                calculatePower(adjP);
                gameFrame.createPiecePowerLabel(adjP);
            }
        }
        calculatePower(piece);
        gameFrame.createPiecePowerLabel(piece);
        gameFrame.repaint();
    }

    @Override
    public void attackPiece(int row, int col, int destRow, int destCol) {
        Piece targetPiece = ((Piece) GameTiles[destRow][destCol]);
        gameFrame.getJLayeredPane().remove(targetPiece.getPieceLabel());
        GameTiles[destRow][destCol] = null;
        movePiece(row, col, destRow, destCol);
        if (targetPiece instanceof Castle) {
            gameFrame.clearMoveLabels();
            sendRequest(Command.GAME_OVER);
            showOutput(gameFrame, " Won the Game.");
            System.exit(0);
        }
        gameFrame.repaint();
    }

    @Override
    public void changeTurn() {
        String lastTurn = Turn.name();
        Turn = Turn.equals(Side.WHITE) ? Side.BLACK : Side.WHITE;
        gameFrame.Turn = Turn;
        int lastIndexOfTurn = gameFrame.getTitle().lastIndexOf(lastTurn);
        gameFrame.setTitle(gameFrame.getTitle().substring(0, lastIndexOfTurn) + gameFrame.getTitle().substring(lastIndexOfTurn).replace(lastTurn, Turn.name()));
    }

    public void calculatePower(Piece piece) {
        piece.resetPower();
        int row = piece.getY();
        int col = piece.getX();
        for (int j = 0; j < ADJACENT_DIRECTIONS.length; j++) {
            try {
                int dirX = ADJACENT_DIRECTIONS[j][0];
                int dirY = ADJACENT_DIRECTIONS[j][1];
                if (GameTiles[row + dirY][col + dirX] != null && GameTiles[row + dirY][col + dirX] instanceof canIncreaseOrDecreaseAdjacentPiecesPower pieceThatCanIncreaseOrDecreaseAdjacentPiecesPower) {
                    pieceThatCanIncreaseOrDecreaseAdjacentPiecesPower.increaseOrDecreaseAdjacentPiecesPower(this, piece);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                // ignore
            }
        }
    }

    public synchronized void addRequestListener() {
        if (RequestListener == null) {
            RequestListener = new Thread(this::requestListenerRunnable);
            RequestListener.start();
        }
    }

    private void requestListenerRunnable() {
        while (socket.isConnected()) {
            try {
                Command Request = (Command) objectInputStream.readObject();
                switch (Request) {
                    case Command.MOVE_PIECE:
                        int row = (Integer) objectInputStream.readObject();
                        int col = (Integer) objectInputStream.readObject();
                        int destRow = (Integer) objectInputStream.readObject();
                        int destCol = (Integer) objectInputStream.readObject();
                        movePiece(row, col, destRow, destCol);
                        break;
                    case Command.ATTACK_PIECE:
                        row = (Integer) objectInputStream.readObject();
                        col = (Integer) objectInputStream.readObject();
                        destRow = (Integer) objectInputStream.readObject();
                        destCol = (Integer) objectInputStream.readObject();
                        attackPiece(row, col, destRow, destCol);
                        break;
                    case Command.CHANGE_TURN:
                        changeTurn();
                        break;
                    case Command.GAME_OVER:
                        showOutput(gameFrame, "Game Over , you lost.");
                        System.exit(0);
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid Request:  " + Request);
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                try {
                    socket.close();
                    e.printStackTrace();
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

public void sendRequest(Object... args) {
    try {
        for (Object arg : args) {
            objectOutputStream.writeObject(arg);
            objectOutputStream.flush();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
