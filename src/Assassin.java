import java.util.ArrayList;
import java.util.List;

public class Assassin extends Piece implements canIncreaseOrDecreaseAdjacentPiecesPower {

    public Assassin(int x, int y, Side Side) {
        super(x, y, Side, 0, 1);
    }

    @Override
    protected String getAssetName() {
        return (this.Side.equals(Side.WHITE) ? 15 : 16) + "_Chess Crusader.png";
    }


    @Override
    protected Integer[][] getMoveDirections(Gameboard gameboard) {
        int[][] moveDirections = Constants.ADJACENT_DIRECTIONS;
        List<Integer[]> possibleMoves = new ArrayList<>();
        int moveRadius = this.moveRadius;
        while (moveRadius > 0) {
            for (int i = 0; i < moveDirections.length; i++) {
                try {
                    if (Gameboard.GameTiles[y + moveDirections[i][1] * moveRadius][x + moveDirections[i][0] * moveRadius] == null) {
                        possibleMoves.add(new Integer[]{x + moveDirections[i][0] * moveRadius, y + moveDirections[i][1] * moveRadius});
                    } else if (Gameboard.GameTiles[y + moveDirections[i][1] * moveRadius][x + moveDirections[i][0] * moveRadius] instanceof Piece piece) {
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
    public void increaseOrDecreaseAdjacentPiecesPower(Gameboard gameboard, Piece targetPiece) {
        try {
            if (!targetPiece.getSide().equals(this.Side)) {
                targetPiece.setTemporaryPower(targetPiece.getTemporaryPower() - 2);
                targetPiece.setPower(targetPiece.getPower() - 2);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            // ignored
        }


    }
}
