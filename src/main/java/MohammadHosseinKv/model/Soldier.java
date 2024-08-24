package main.java.MohammadHosseinKv.model;

import main.java.MohammadHosseinKv.logic.GameBoard;
import main.java.MohammadHosseinKv.logic.canIncreaseOrDecreaseAdjacentPiecesPower;
import main.java.MohammadHosseinKv.util.Constants;

import static main.java.MohammadHosseinKv.util.Constants.RESOURCES_FOLDER_PATH;
import static main.java.MohammadHosseinKv.util.Util.*;
import static main.java.MohammadHosseinKv.model.Side.*;

import java.util.ArrayList;
import java.util.List;

public class Soldier extends Piece implements canIncreaseOrDecreaseAdjacentPiecesPower {

    public Soldier(int x, int y, Side Side) {
        super(x, y, Side, 1, 1);
    }

    @Override
    public String getAssetResourcePath() {
        return RESOURCES_FOLDER_PATH + (this.Side.equals(WHITE) ? 19 : 20) + "_Chess Crusader.png";
    }


    @Override
    public Integer[][] getMoveDirections() {
        int[][] moveDirections;
        List<Integer[]> possibleMoves = new ArrayList<>();
        moveDirections = switch (this.Side) {
            case WHITE -> new int[][]{SOUTH_WEST_DIRECTION, SOUTH_DIRECTION, SOUTH_EAST_DIRECTION};
            case BLACK -> new int[][]{NORTH_WEST_DIRECTION, NORTH_DIRECTION, NORTH_EAST_DIRECTION};
        };

        int moveRadius = this.moveRadius;
        while (moveRadius > 0) {
            for (int[] moveDirection : moveDirections) {
                int row = y + moveDirection[1] * moveRadius;
                int col = x + moveDirection[0] * moveRadius;
                if (coordinateIsInGameBounds(row, col)) {
                    if (GameBoard.GameTiles[row][col] == null) {
                        possibleMoves.add(new Integer[]{col, row});
                    } else if (GameBoard.GameTiles[row][col] instanceof Piece piece) {
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
    public void increaseOrDecreaseAdjacentPiecesPower(Piece targetPiece) {
        if (targetPiece.getSide().equals(this.Side) && targetPiece instanceof Soldier) {
            targetPiece.setTemporaryPower(targetPiece.getTemporaryPower() + 1);
            targetPiece.setPower(targetPiece.getPower() + 1);
        }
    }
}
