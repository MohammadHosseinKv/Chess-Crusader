public interface Gameboard {

    Object[][] GameTiles = new Object[8][8];

    void initGameboardPieces();

    void movePiece(int row, int col, int destRow, int destCol);

    void attackPiece(int row, int col, int destRow, int destCol);

    void changeTurn();

}
