package util;

import logic.GameBoard;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public final class Util {

    // Directions coordinate
    public static final int[] WEST_DIRECTION = {-1, 0};
    public static final int[] EAST_DIRECTION = {1, 0};
    public static final int[] NORTH_DIRECTION = {0, -1};
    public static final int[] SOUTH_DIRECTION = {0, 1};
    public static final int[] NORTH_WEST_DIRECTION = {WEST_DIRECTION[0], NORTH_DIRECTION[1]};
    public static final int[] NORTH_EAST_DIRECTION = {EAST_DIRECTION[0], NORTH_DIRECTION[1]};
    public static final int[] SOUTH_WEST_DIRECTION = {WEST_DIRECTION[0], SOUTH_DIRECTION[1]};
    public static final int[] SOUTH_EAST_DIRECTION = {EAST_DIRECTION[0], SOUTH_DIRECTION[1]};
    public static final int[][] ADJACENT_DIRECTIONS = {WEST_DIRECTION, EAST_DIRECTION, NORTH_DIRECTION, SOUTH_DIRECTION,
            NORTH_WEST_DIRECTION, NORTH_EAST_DIRECTION, SOUTH_WEST_DIRECTION, SOUTH_EAST_DIRECTION};

    public static Set<Piece> getAdjacentPieces(int row, int col) {
        Set<Piece> adjacentPieces = new HashSet<>();
        for (int[] adjacentDirection : ADJACENT_DIRECTIONS) {
            int dirX = adjacentDirection[0];
            int dirY = adjacentDirection[1];
            if (coordinateIsInGameBounds(row + dirY, col + dirX)) {
                if (GameBoard.GameTiles[row + dirY][col + dirX] != null && GameBoard.GameTiles[row + dirY][col + dirX] instanceof Piece piece) {
                    adjacentPieces.add(piece);
                }
            }
        }
        return adjacentPieces;
    }

    public static boolean coordinateIsInGameBounds(int row, int col) {
        return row > 0 && row < GameBoard.GameTiles.length && col > 0 && col < GameBoard.GameTiles[0].length;
    }

    public static boolean showOutput(Component parentComponent, Object outputMessage) {
        try {
            JOptionPane.showMessageDialog(parentComponent, outputMessage);
            return true;
        } catch (HeadlessException e) {
            return false;
        }
    }

    public static boolean centralizeFrame(JFrame frame) {
        try {
            Point CenterPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
            frame.setLocation(new Point(CenterPoint.x - (frame.getPreferredSize().width / 2), CenterPoint.y - (frame.getPreferredSize().height / 2)));
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private Util() {
    }
}
