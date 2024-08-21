import javax.swing.*;

public abstract class Piece {

    protected int x;
    protected int y;
    protected Side Side;
    protected int Power;
    protected int temporaryPower;
    protected boolean Selected;
    protected JLabel pieceLabel;
    protected int moveRadius;

    protected abstract String getAssetName();

    protected abstract Integer[][] getMoveDirections(Gameboard gameboard);

    protected void resetPower() {
        Power = Power - temporaryPower;
        temporaryPower = 0;
    }

    protected String getPowerAssetName() {
        return (Power + 26) + "_Chess Crusader.png";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Side getSide() {
        return Side;
    }

    public void setSide(Side side) {
        Side = side;
    }

    public int getPower() {
        return Power;
    }

    public void setPower(int initialPower) {
        this.Power = initialPower;
    }

    public int getTemporaryPower() {
        return temporaryPower;
    }

    public void setTemporaryPower(int temporaryPower) {
        this.temporaryPower = temporaryPower;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }

    public JLabel getPieceLabel() {
        return pieceLabel;
    }

    public void setPieceLabel(JLabel pieceLabel) {
        this.pieceLabel = pieceLabel;
    }

    public int getMoveRadius() {
        return moveRadius;
    }

    public void setMoveRadius(int moveRadius) {
        this.moveRadius = moveRadius;
    }

    public Piece(int x, int y, Side Side, int Power, int moveRadius) {
        this.x = x;
        this.y = y;
        this.Side = Side;
        this.Power = Power;
        this.moveRadius = moveRadius;
    }


}
