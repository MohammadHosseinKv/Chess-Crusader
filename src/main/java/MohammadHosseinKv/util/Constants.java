package main.java.MohammadHosseinKv.util;

import main.java.MohammadHosseinKv.logic.*;
import main.java.MohammadHosseinKv.model.*;

import java.awt.*;

public class Constants {

    public static final String SERVER_IP = "127.0.0.1"; // localhost
    public static final int SERVER_PORT = 8080;
    private static final int GAME_BOARD_COLUMN = GameBoard.GameTiles[0].length;
    private static final int GAME_BOARD_ROW = GameBoard.GameTiles.length;
    public static final Dimension TILE_DIMENSION = new Dimension(80, 80);
    public static final Dimension GAME_DIMENSION = new Dimension(TILE_DIMENSION.width * GAME_BOARD_COLUMN, TILE_DIMENSION.height * GAME_BOARD_ROW);
    public static final Dimension FRAME_DIMENSION = new Dimension(TILE_DIMENSION.width * GAME_BOARD_COLUMN, TILE_DIMENSION.height * GAME_BOARD_ROW);
    public static final int NUMBER_OF_TILES = (GAME_DIMENSION.width * GAME_DIMENSION.height) / (TILE_DIMENSION.width * TILE_DIMENSION.height);
    public static final Side INITIAL_TURN_SIDE = Side.WHITE;
    // Resources Path
    public static final String RESOURCES_FOLDER_PATH = "assets/";
    public static final String GAME_DOCUMENT_MD_FILE_URL_PATH = "https://github.com/MohammadHosseinKv/Chess-Crusader/blob/master/README.md";
    public static final String SELECTED_PIECE_BACKGROUND_RESOURCE_PATH = RESOURCES_FOLDER_PATH + "13_Chess Crusader.png";
    public static final String MOVE_CIRCLE_RESOURCE_PATH = RESOURCES_FOLDER_PATH + "14_Chess Crusader.png";
    public static final String GAME_BOARD_BACKGROUND_RESOURCE_PATH = RESOURCES_FOLDER_PATH + "36_Chess Crusader.png";
    public static final String START_BACKGROUND_RESOURCE_PATH = RESOURCES_FOLDER_PATH + "welcomebg.png";
    public static final String START_TITLE_RESOURCE_PATH = RESOURCES_FOLDER_PATH + "ccTitle.png";
    // GitHub Repository
    public static final String GITHUB_REPOSITORY = "https://github.com/MohammadHosseinKv/Chess-Crusader";


}
