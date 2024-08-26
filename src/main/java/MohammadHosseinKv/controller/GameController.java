package main.java.MohammadHosseinKv.controller;

import main.java.MohammadHosseinKv.gui.*;
import main.java.MohammadHosseinKv.logic.*;
import main.java.MohammadHosseinKv.model.*;
import main.java.MohammadHosseinKv.network.*;

import javax.swing.*;
import java.io.IOException;

import static main.java.MohammadHosseinKv.network.Command.*;
import static main.java.MohammadHosseinKv.util.Constants.*;
import static main.java.MohammadHosseinKv.util.Util.*;

public class GameController {
    private Game game;
    private GameFrame gameFrame;
    private SocketManager socketManager;
    private Side side;

    public GameController(Side side, SocketManager socketManager) {
        this.socketManager = socketManager;
        this.gameFrame = new GameFrame(side, this, INITIAL_TURN_SIDE);
        this.game = new Game(side, this, INITIAL_TURN_SIDE);
        this.side = side;
        start();
    }

    public void start() {
        game.initGameBoardPieces();
        gameFrame.initUI();
        game.addRequestListener();
    }

    public void movePiece(int row, int col, int destRow, int destCol, boolean informOpponent) {
        game.movePiece(row, col, destRow, destCol);
        gameFrame.updateUIAfterMove(row, col, destRow, destCol);
        if (informOpponent) {
            try {
                socketManager.sendRequest(MOVE_PIECE, row, col, destRow, destCol);
            } catch (IOException e) {
                showOutput(gameFrame, e.getMessage());
                e.printStackTrace();
            }
        }
        changeTurn();
    }

    public void attackPiece(int row, int col, int destRow, int destCol, boolean informOpponent) {
        game.attackPiece(row, col, destRow, destCol);
        gameFrame.updateUIAfterAttack(row, col, destRow, destCol);
        if (informOpponent) {
            try {
                socketManager.sendRequest(ATTACK_PIECE, row, col, destRow, destCol);
            } catch (IOException e) {
                showOutput(gameFrame, e.getMessage());
                e.printStackTrace();
            }
        }
        changeTurn();
    }

    public void changeTurn() {
        game.changeTurn();
        gameFrame.updateTurn(game.getTurn());
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
                        case ATTACK_PIECE:
                            row = (Integer) socketManager.readRequest();
                            col = (Integer) socketManager.readRequest();
                            destRow = (Integer) socketManager.readRequest();
                            destCol = (Integer) socketManager.readRequest();
                            attackPiece(row, col, destRow, destCol, false);
                            break;
                        case CHANGE_TURN:
                            changeTurn();
                            break;
                        case GAME_OVER:
                            Side winner = (Side) socketManager.readRequest();
                            gameOver(winner, false);
                            break;
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

    public void gameOver(Side Winner, boolean informOpponent) {
        if (informOpponent) {
            try {
                socketManager.sendRequest(GAME_OVER, Winner);
            } catch (IOException e) {
                showOutput(gameFrame, e.getMessage());
            }
        }
        showOutput(gameFrame, "Game Over , " + (side == Winner ? "You won the game." : "You lost the game."));
        returnToStartFrame();
    }

    private void returnToStartFrame() {
        gameFrame.dispose();
        SwingUtilities.invokeLater(StartFrame::new);
    }
}
