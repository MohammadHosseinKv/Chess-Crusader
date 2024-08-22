package util;

import java.awt.*;

public class Constants {

    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 8080;
    public static final Dimension TILE_DIMENSION = new Dimension(80,80);
    public static final Dimension GAME_DIMENSION = new Dimension(640,640);
    public static final Dimension FRAME_DIMENSION = new Dimension(640,640);
    public static final int NUMBER_OF_TILES = (GAME_DIMENSION.width * GAME_DIMENSION.height) / (TILE_DIMENSION.width * TILE_DIMENSION.height);
    // Resources Path
    public static final String SELECTED_PIECE_BACKGROUND_RESOURCE_PATH = "13_Chess Crusader.png";
    public static final String MOVE_CIRCLE_RESOURCE_PATH = "14_Chess Crusader.png";
    public static final String GAME_BOARD_BACKGROUND_RESOURCE_PATH = "36_Chess Crusader.png";
    public static final String START_BACKGROUND_RESOURCE_PATH = "welcomebg.png";
    public static final String START_TITLE_RESOURCE_PATH = "ccTitle.png";


}
