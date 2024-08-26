package main.java.MohammadHosseinKv.model;

import main.java.MohammadHosseinKv.logic.*;

import java.util.*;

import static main.java.MohammadHosseinKv.model.Side.*;
import static main.java.MohammadHosseinKv.util.Constants.*;
import static main.java.MohammadHosseinKv.util.Util.*;

public class Castle extends Piece implements canAdjustAdjacentPiecesPower {

    public Castle(int x, int y, Side Side) {
        super(x, y, Side, 1, 1);
    }

    @Override
    public String getAssetResourcePath() {
        return RESOURCES_FOLDER_PATH + (this.Side.equals(WHITE) ? 23 : 24) + "_Chess Crusader.png";
    }


    @Override
    public Integer[][] getMoveDirections() {
        int[][] moveDirections = ADJACENT_DIRECTIONS;
        List<Integer[]> possibleMoves = new ArrayList<>();

        int moveRadius = this.moveRadius;
        while (moveRadius > 0) {
            for (int i = 0; i < moveDirections.length; i++) {
                int row = y + moveDirections[i][1] * moveRadius;
                int col = x + moveDirections[i][0] * moveRadius;
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
        if (targetPiece.getSide().equals(this.Side)) {
            targetPiece.setTemporaryPower(targetPiece.getTemporaryPower() + 1);
            targetPiece.setPower(targetPiece.getPower() + 1);
        }
    }

}
