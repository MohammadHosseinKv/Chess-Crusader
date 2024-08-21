public class Constants {

    public static final int TILE_WIDTH = 80;
    public static final int TILE_HEIGHT = 80;
    public static final int[][] ADJACENT_DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}};
    public static final int LAYEREDPANE_WIDTH = 640;
    public static final int LAYEREDPANE_HEIGHT = 640;
    public static final int FRAME_WIDTH = 640;
    public static final int FRAME_HEIGHT = 640;
    public static final int NUMBER_OF_TILES = (LAYEREDPANE_WIDTH * LAYEREDPANE_HEIGHT) / (TILE_WIDTH * TILE_HEIGHT);
    public static final String SELECTED_PIECE_BACKGROUND_ASSET_NAME = "13_Chess Crusader.png";
    public static final String MOVE_CIRCLE_ASSET_NAME = "14_Chess Crusader.png";
    public static final String GAMEBOARD8x8_BACKGROUND_ASSET_NAME = "36_Chess Crusader.png";
    public static final String START_BACKGROUND_ASSET_NAME = "welcomebg.png";
    public static final String START_TITLE_ASSET_NAME = "ccTitle.png";


}
