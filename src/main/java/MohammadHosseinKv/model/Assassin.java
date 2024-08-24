package main.java.MohammadHosseinKv.model;

import main.java.MohammadHosseinKv.logic.GameBoard;
import main.java.MohammadHosseinKv.logic.canIncreaseOrDecreaseAdjacentPiecesPower;
import main.java.MohammadHosseinKv.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static main.java.MohammadHosseinKv.model.Side.WHITE;
import static main.java.MohammadHosseinKv.util.Constants.RESOURCES_FOLDER_PATH;
import static main.java.MohammadHosseinKv.util.Util.ADJACENT_DIRECTIONS;
import static main.java.MohammadHosseinKv.util.Util.coordinateIsInGameBounds;

public class Assassin extends Piece implements canIncreaseOrDecreaseAdjacentPiecesPower {

    public Assassin(int x, int y, Side Side) {
        super(x, y, Side, 0, 1);
    }

    @Override
    public String getAssetResourcePath() {
        return RESOURCES_FOLDER_PATH + (this.Side.equals(WHITE) ? 15 : 16) + "_Chess Crusader.png";
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
        if (!targetPiece.getSide().equals(this.Side)) {
            targetPiece.setTemporaryPower(targetPiece.getTemporaryPower() - 2);
            targetPiece.setPower(targetPiece.getPower() - 2);
        }

    }
}
