package logic;

import model.Piece;

public interface canIncreaseOrDecreaseAdjacentPiecesPower {
    void increaseOrDecreaseAdjacentPiecesPower(GameBoard gameboard, Piece targetPiece);
}
