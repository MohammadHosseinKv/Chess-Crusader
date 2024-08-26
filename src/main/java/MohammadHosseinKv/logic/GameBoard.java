package main.java.MohammadHosseinKv.logic;

public interface GameBoard {

    Object[][] GameTiles = new Object[8][8];

    void initGameBoardPieces();

    void movePiece(int row,int col, int destRow, int destCol);

    void attackPiece(int row,int col, int destRow, int destCol);

    void changeTurn();

}
