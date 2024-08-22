package model;

import logic.GameBoard;

import java.util.ArrayList;
import java.util.List;

import static util.Util.ADJACENT_DIRECTIONS;

public class Knight extends Piece {

    public Knight(int x, int y, Side Side) {
        super(x, y, Side, 1, 2);
    }

    @Override
    public String getAssetName() {
        return (this.Side.equals(Side.WHITE) ? 17 : 18) + "_Chess Crusader.png";
    }


    @Override
    public Integer[][] getMoveDirections(GameBoard gameboard) {
        int[][] moveDirections = ADJACENT_DIRECTIONS;
        List<Integer[]> possibleMoves = new ArrayList<>();
        int moveRadius = this.moveRadius;
        while (moveRadius > 0) {
            for (int i = 0; i < moveDirections.length; i++) {
                try {
                    if (GameBoard.GameTiles[y + (moveDirections[i][1] * moveRadius)][x + (moveDirections[i][0] * moveRadius)] == null) {
                        possibleMoves.add(new Integer[]{x + (moveDirections[i][0] * moveRadius), y + (moveDirections[i][1] * moveRadius)});
                    } else if (GameBoard.GameTiles[y + (moveDirections[i][1] * moveRadius)][x + (moveDirections[i][0] * moveRadius)] instanceof Piece piece) {
                        if (!piece.getSide().equals(this.Side) && piece.getPower() <= this.Power) {
                            possibleMoves.add(new Integer[]{x + (moveDirections[i][0] * moveRadius), y + (moveDirections[i][1] * moveRadius)});
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // ignore
                }
            }
            moveRadius--;
        }
//        return Util.storeDirectionIntegerArrayListinArray(possibleMoves);
        return possibleMoves.toArray(new Integer[0][0]);
    }

}
