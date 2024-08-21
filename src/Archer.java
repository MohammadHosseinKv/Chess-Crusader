import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {

    public Archer(int x, int y, Side Side) {
        super(x, y, Side, 2, 1);
    }

    @Override
    protected String getAssetName() {
        return (this.Side.equals(Side.WHITE) ? 21 : 22) + "_Chess Crusader.png";
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
}
