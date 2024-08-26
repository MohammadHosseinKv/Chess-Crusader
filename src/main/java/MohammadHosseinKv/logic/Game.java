package main.java.MohammadHosseinKv.logic;

import main.java.MohammadHosseinKv.controller.GameController;
import main.java.MohammadHosseinKv.model.*;

import static main.java.MohammadHosseinKv.model.Side.*;
import static main.java.MohammadHosseinKv.util.Util.*;

import java.util.*;

public class Game implements GameBoard {

    private Side side;
    private Side Turn;
    private GameController controller;

    public Game(Side side, GameController controller, Side initialTurn) {
        this.side = side;
        this.Turn = initialTurn;
        this.controller = controller;
    }

    @Override
    public void initGameBoardPieces() {
        // fill game board with null pointer
        for (Object[] GameTilesRow : GameTiles)
            Arrays.fill(GameTilesRow, null);

        // initialize white pieces
        GameTiles[0][0] = new Archer(0, 0, WHITE);
        GameTiles[0][1] = new Archer(1, 0, WHITE);
        GameTiles[0][2] = new Knight(2, 0, WHITE);
        GameTiles[0][3] = new Castle(3, 0, WHITE);
        GameTiles[0][4] = new Assassin(4, 0, WHITE);
        GameTiles[0][5] = new Knight(5, 0, WHITE);
        GameTiles[0][6] = new Archer(6, 0, WHITE);
        GameTiles[0][7] = new Archer(7, 0, WHITE);
        for (int i = 0; i < GameTiles[1].length; i++)
            GameTiles[1][i] = new Soldier(i, 1, WHITE);

        // initialize black pieces
        GameTiles[7][0] = new Archer(0, 7, BLACK);
        GameTiles[7][1] = new Archer(1, 7, BLACK);
        GameTiles[7][2] = new Knight(2, 7, BLACK);
        GameTiles[7][3] = new Assassin(3, 7, BLACK);
        GameTiles[7][4] = new Castle(4, 7, BLACK);
        GameTiles[7][5] = new Knight(5, 7, BLACK);
        GameTiles[7][6] = new Archer(6, 7, BLACK);
        GameTiles[7][7] = new Archer(7, 7, BLACK);
        for (int i = 0; i < GameTiles[6].length; i++)
            GameTiles[6][i] = new Soldier(i, 6, BLACK);

        // calculate pieces power
        for (int i = 0; i < GameTiles.length; i++) {
            for (int j = 0; j < GameTiles[i].length; j++) {
                if (GameTiles[i][j] != null && GameTiles[i][j] instanceof Piece) {
                    Piece piece = (Piece) GameTiles[i][j];
                    calculatePower(piece);
                }
            }
        }
    }

    @Override
    public void movePiece(int row, int col, int destRow, int destCol) {
        Piece piece = (Piece) GameTiles[row][col];
        Set<Piece> adjacentPieces = new HashSet<>();
        if (piece instanceof canAdjustAdjacentPiecesPower) {
            adjacentPieces.addAll(getAdjacentPieces(row, col));
            adjacentPieces.addAll(getAdjacentPieces(destRow, destCol));
        }
        GameTiles[destRow][destCol] = piece;
        GameTiles[row][col] = null;
        piece.setX(destCol);
        piece.setY(destRow);
        if (!adjacentPieces.isEmpty())
            for (Piece p : adjacentPieces)
                calculatePower(p);
        calculatePower(piece);
    }

    @Override
    public void attackPiece(int row, int col, int destRow, int destCol) {
        if (checkWinCondition(destRow, destCol)) return;
        GameTiles[destRow][destCol] = null;
        movePiece(row, col, destRow, destCol);
    }

    private boolean checkWinCondition(int destRow, int destCol) {
        if (GameTiles[destRow][destCol] != null && GameTiles[destRow][destCol] instanceof Castle) {
            Castle castle = (Castle) GameTiles[destRow][destCol];
            if (castle.getSide() != this.side) {
                controller.gameOver(this.side, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void changeTurn() {
        Turn = Turn.equals(WHITE) ? BLACK : WHITE;
    }

    public Side getTurn() {
        return Turn;
    }

    public void addRequestListener() {
        controller.handleIncomingRequest();
    }

    public void calculatePower(Piece piece) {
        piece.resetPower();
        int row = piece.getY();
        int col = piece.getX();
        for (int j = 0; j < ADJACENT_DIRECTIONS.length; j++) {
            int dirX = ADJACENT_DIRECTIONS[j][0];
            int dirY = ADJACENT_DIRECTIONS[j][1];
            if (coordinateIsInGameBounds(row + dirY, col + dirX)) {
                if (GameTiles[row + dirY][col + dirX] != null && GameTiles[row + dirY][col + dirX] instanceof canAdjustAdjacentPiecesPower) {
                    canAdjustAdjacentPiecesPower pieceThatCanAdjustAdjacentPiecesPower = (canAdjustAdjacentPiecesPower) GameTiles[row + dirY][col + dirX];
                    pieceThatCanAdjustAdjacentPiecesPower.adjustAdjacentPiecesPower(piece);
                }
            }
        }
    }

}
