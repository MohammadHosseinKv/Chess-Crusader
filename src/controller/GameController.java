package controller;

import gui.GameFrame;
import logic.Command;
import logic.Game;
import logic.GameBoard;
import model.Piece;
import model.Side;
import network.SocketManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static logic.Command.*;
import static util.Util.showOutput;

public class GameController {
    private Game game;
    private GameFrame gameFrame;
    private SocketManager socketManager;

    public GameController(Side side, SocketManager socketManager) {
        this.socketManager = socketManager;
        this.gameFrame = new GameFrame(side, this);
        this.game = new Game(side, this);
    }

    public void start() {
        game.initGameBoardPieces();
        gameFrame.initUI();
        game.addRequestListener();
    }

    public void movePiece(int row, int col, int destRow, int destCol, boolean informOpponent) {
        game.movePiece(row, col, destRow, destCol);
        gameFrame.updateUIAfterMove(row, col, destRow, destCol);
        changeTurn(informOpponent);
        if (informOpponent) {
            try {
                socketManager.sendRequest(MOVE_PIECE, row, col, destRow, destCol);
            } catch (IOException e) {
                showOutput(gameFrame, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void attackPiece(int row, int col, int destRow, int destCol, boolean informOpponent) {
        game.attackPiece(row, col, destRow, destCol);
        gameFrame.updateUIAfterAttack(row, col, destRow, destCol);
        changeTurn(informOpponent);
        if (informOpponent) {
            try {
                socketManager.sendRequest(ATTACK_PIECE, row, col, destRow, destCol);
            } catch (IOException e) {
                showOutput(gameFrame, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void changeTurn(boolean informOpponent) {
        game.changeTurn();
        gameFrame.updateTurn(game.getTurn());
        if (informOpponent) {
            try{
                socketManager.sendRequest(CHANGE_TURN);
            }catch (IOException e){
                showOutput(gameFrame, e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void handleIncomingRequest() {
        new Thread(() -> {
            try {
                while (true) {
                    Command Request = (Command) socketManager.readRequest();
                    switch (Request) {
                        case MOVE_PIECE:
                            int row = (Integer) socketManager.readRequest();
                            int col = (Integer) socketManager.readRequest();
                            int destRow = (Integer) socketManager.readRequest();
                            int destCol = (Integer) socketManager.readRequest();
                            movePiece(row, col, destRow, destCol, false);
                            break;
                        case Command.ATTACK_PIECE:
                            row = (Integer) socketManager.readRequest();
                            col = (Integer) socketManager.readRequest();
                            destRow = (Integer) socketManager.readRequest();
                            destCol = (Integer) socketManager.readRequest();
                            attackPiece(row, col, destRow, destCol, false);
                            break;
                        case CHANGE_TURN:
                            changeTurn(false);
                            break;
                        case Command.GAME_OVER:
                            showOutput(gameFrame, "Game Over , you lost.");
                            System.exit(0);
                        default:
                            showOutput(gameFrame, "Invalid Request:  " + Request);
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                try {
                    socketManager.close();
                    e.printStackTrace();
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
