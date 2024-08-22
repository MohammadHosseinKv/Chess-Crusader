package model;

import logic.GameBoard;
import logic.canIncreaseOrDecreaseAdjacentPiecesPower;

import java.util.ArrayList;
import java.util.List;

public class Soldier extends Piece implements canIncreaseOrDecreaseAdjacentPiecesPower {

    public Soldier(int x, int y, Side Side) {
        super(x, y, Side, 1, 1);
    }

    @Override
    public String getAssetName() {
        return (this.Side.equals(Side.WHITE) ? 19 : 20) + "_Chess Crusader.png";
    }


    @Override
    public Integer[][] getMoveDirections(GameBoard gameboard) {
        int[][] moveDirections;
        List<Integer[]> possibleMoves = new ArrayList<>();
        moveDirections = switch (this.Side) {
            case Side.WHITE -> new int[][]{{1, 1}, {0, 1}, {-1, 1}};
            case Side.BLACK -> new int[][]{{-1, -1}, {0, -1}, {1, -1}};
        };

        int moveRadius = this.moveRadius;
        while (moveRadius > 0) {
            for (int i = 0; i < moveDirections.length; i++) {
                try {
                    if (GameBoard.GameTiles[y + moveDirections[i][1] * moveRadius][x + moveDirections[i][0] * moveRadius] == null) {
                        possibleMoves.add(new Integer[]{x + moveDirections[i][0] * moveRadius, y + moveDirections[i][1] * moveRadius});
                    } else if (GameBoard.GameTiles[y + moveDirections[i][1] * moveRadius][x + moveDirections[i][0] * moveRadius] instanceof Piece piece) {
                        if (!piece.getSide().equals(this.Side) && piece.getPower() <= this.Power) {
                            possibleMoves.add(new Integer[]{x + moveDirections[i][0] * moveRadius, y + moveDirections[i][1] * moveRadius});
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // ignore
                }
            }
            moveRadius--;
        }

        return possibleMoves.toArray(new Integer[0][0]);
    }

    @Override
    public void increaseOrDecreaseAdjacentPiecesPower(GameBoard gameboard, Piece targetPiece) {
        try {
            if (targetPiece.getSide().equals(this.Side) && targetPiece instanceof Soldier) {
                targetPiece.setTemporaryPower(targetPiece.getTemporaryPower() + 1);
                targetPiece.setPower(targetPiece.getPower() + 1);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            // ignored
        }
    }
}
