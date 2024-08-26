package main.java.MohammadHosseinKv.model;

import main.java.MohammadHosseinKv.logic.*;

import static main.java.MohammadHosseinKv.util.Constants.*;
import static main.java.MohammadHosseinKv.util.Util.*;
import static main.java.MohammadHosseinKv.model.Side.*;

import java.util.*;

public class Soldier extends Piece implements canAdjustAdjacentPiecesPower {

    public Soldier(int x, int y, Side Side) {
        super(x, y, Side, 1, 1);
    }

    @Override
    public String getAssetResourcePath() {
        return RESOURCES_FOLDER_PATH + (this.Side.equals(WHITE) ? 19 : 20) + "_Chess Crusader.png";
    }


    @Override
    public Integer[][] getMoveDirections() {
        int[][] moveDirections = new int[0][];
        List<Integer[]> possibleMoves = new ArrayList<>();
        switch (this.Side) {
            case WHITE:
                moveDirections = new int[][]{SOUTH_WEST_DIRECTION, SOUTH_DIRECTION, SOUTH_EAST_DIRECTION};
                break;
            case BLACK:
                moveDirections = new int[][]{NORTH_WEST_DIRECTION, NORTH_DIRECTION, NORTH_EAST_DIRECTION};
                break;
        }

        int moveRadius = this.moveRadius;
        while (moveRadius > 0) {
            for (int[] moveDirection : moveDirections) {
                int row = y + moveDirection[1] * moveRadius;
                int col = x + moveDirection[0] * moveRadius;
                if (coordinateIsInGameBounds(row, col)) {
                    if (GameBoard.GameTiles[row][col] == null) {
                        possibleMoves.add(new Integer[]{col, row});
                    } else if (GameBoard.GameTiles[row][col] instanceof Piece) {
                        Piece piece = (Piece) GameBoard.GameTiles[row][col];
                        if (!piece.getSide().equals(this.Side) && piece.getPower() <= this.Power) {
                            possibleMoves.add(new Integer[]{col, row});
                        }
                    }
                }
            }
            moveRadius--;
        }

        return possibleMoves.toArray(new Integer[0][0]);
    }

    @Override
    public void adjustAdjacentPiecesPower(Piece targetPiece) {
        if (targetPiece.getSide().equals(this.Side) && targetPiece instanceof Soldier) {
            targetPiece.setTemporaryPower(targetPiece.getTemporaryPower() + 1);
            targetPiece.setPower(targetPiece.getPower() + 1);
        }
    }
}
