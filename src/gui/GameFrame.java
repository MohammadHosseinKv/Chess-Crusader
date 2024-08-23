package gui;

import controller.GameController;
import logic.Game;
import logic.GameBoard;
import logic.canIncreaseOrDecreaseAdjacentPiecesPower;
import model.Piece;
import model.Side;

import static logic.Command.*;
import static util.Constants.*;
import static util.Util.*;
import static util.Util.getAdjacentPieces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class GameFrame extends JFrame {

    GameController controller;
    JLayeredPane layeredPane;
    Side side;
    Map<Piece, List<JLabel>> moveLabels = new HashMap<>();
    Map<Point,JLabel> pieceLabelMap = new HashMap<>();
    int TILE_WIDTH = TILE_DIMENSION.width;
    int TILE_HEIGHT = TILE_DIMENSION.height;

    public GameFrame(Side side, Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        super();
        setTitle("Chess Crusader - You Are " + side.name() + " | Turn: " + Turn.name());
        setResizable(false);
        getContentPane().setPreferredSize(FRAME_DIMENSION);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(GAME_DIMENSION);
        layeredPane.setOpaque(true);
        layeredPane.setVisible(true);
        add(layeredPane);
        game = new Game(Turn, socket,objectOutputStream, objectInputStream, side, this);
        game.addRequestListener();
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.socket = socket;
        this.side = side;

        centralizeFrame(this);
    }

    public GameFrame(Side side, GameController controller) {
        this.side = side;
        this.controller = controller;
        // Initialize UI components





        centralizeFrame(this);
    }

    public JLabel getPieceLabelAt(int row,int col){
        return pieceLabelMap.get(new Point(col,row));
    }

    public void removePieceFromBoard(JLabel pieceLabel){
        layeredPane.remove(pieceLabel);
        layeredPane.repaint();
        pieceLabelMap.values().remove(pieceLabel);
    }

    public void addPieceToBoard(int row,int col,JLabel pieceLabel){
        Point piecePosition = new Point(col,row);
        pieceLabelMap.put(piecePosition,pieceLabel);
        layeredPane.add(pieceLabel,PIECE_LAYER);
        pieceLabel.setLocation(computeLocation(row,col));
        layeredPane.repaint();
    }

    public void removePiece(int row,int col){
        Point piecePosition = new Point(col,row);
        JLabel pieceLabel = pieceLabelMap.get(piecePosition);
        if(pieceLabel != null){
            removePieceFromBoard(pieceLabel);
            pieceLabelMap.remove(piecePosition);
        }
    }

    private Point computeLocation(int row,int col){
        return new Point(col*TILE_WIDTH,row*TILE_HEIGHT);
    }

    public void initUI() {
        int GAME_WIDTH = GAME_DIMENSION.width;
        int GAME_HEIGHT = GAME_DIMENSION.height;
        int TILE_WIDTH = TILE_DIMENSION.width;
        int TILE_HEIGHT = TILE_DIMENSION.height;

        // add 8x8 gameboard background to layeredpane
        ImageIcon backGroundImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + GAME_BOARD_BACKGROUND_RESOURCE_PATH)));
        backGroundImage.setImage(backGroundImage.getImage().getScaledInstance(GAME_WIDTH, GAME_HEIGHT, Image.SCALE_AREA_AVERAGING));
        JLabel backGroundLabel = new JLabel(backGroundImage);
        backGroundLabel.setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
        layeredPane.add(backGroundLabel, Integer.valueOf(10));

        game.initGameBoardPieces();
        // Fill screen with piece labels
        int x = 0;
        int y = 0;
        for (int i = 0; i < NUMBER_OF_TILES; i++) {

            int row = y / TILE_HEIGHT;
            int col = x / TILE_WIDTH;

            if (game.GameTiles[row][col] != null && game.GameTiles[row][col] instanceof Piece piece) {

                JLabel pieceLabel = createPieceLabel(piece);
                piece.setPieceLabel(pieceLabel);
                game.calculatePower(piece);
                createPiecePowerLabel(piece);
                pieceLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (side.equals(Turn) && piece.getSide().equals(side)) {
                                if (piece.isSelected()) {
                                    piece.setSelected(false);
                                    clearMoveLabels();
                                } else {
                                    piece.setSelected(true);
                                    int pieceCol = piece.getX();
                                    int pieceRow = piece.getY();
                                    JLabel selectedPieceBgLabel = createSelectedPieceBackgroundLabel(pieceCol, pieceRow);
                                    clearMoveLabels();
                                    List<JLabel> selectedPieceMoveLabels = new ArrayList<>();
                                    selectedPieceMoveLabels.add(selectedPieceBgLabel);
                                    layeredPane.add(selectedPieceBgLabel, Integer.valueOf(20));

                                    Integer[][] pieceMoveDirections = piece.getMoveDirections(game);
                                    for (int i = 0; i < pieceMoveDirections.length; i++) {
                                        int moveLabelCol = pieceMoveDirections[i][0];
                                        int moveLabelRow = pieceMoveDirections[i][1];
                                        JLabel pieceMoveLabel = createPieceMoveLabel(moveLabelCol, moveLabelRow);
                                        selectedPieceMoveLabels.add(pieceMoveLabel);
                                        layeredPane.add(pieceMoveLabel, Integer.valueOf(200));
                                        layeredPane.repaint();
                                        pieceMoveLabel.addMouseListener(new MouseAdapter() {
                                            @Override
                                            public void mouseClicked(MouseEvent e) {
                                                int destRow = pieceMoveLabel.getY() / TILE_HEIGHT;
                                                int destCol = pieceMoveLabel.getX() / TILE_WIDTH;
                                                if (game.GameTiles[destRow][destCol] == null) {
                                                    piece.setSelected(false);
                                                    game.movePiece(pieceRow, pieceCol, destRow, destCol);
                                                    clearMoveLabels();
                                                    game.changeTurn();
                                                    game.sendRequest(MOVE_PIECE, pieceRow, pieceCol, destRow, destCol);
                                                    game.sendRequest(CHANGE_TURN);
                                                } else {
                                                    piece.setSelected(false);
                                                    game.attackPiece(pieceRow, pieceCol, destRow, destCol);
                                                    clearMoveLabels();
                                                    game.changeTurn();
                                                    game.sendRequest(ATTACK_PIECE, pieceRow, pieceCol, destRow, destCol);
                                                    game.sendRequest(CHANGE_TURN);
                                                }
                                            }
                                        });
                                    }
                                    moveLabels.put(piece, selectedPieceMoveLabels);

                                }
                            } else if (!side.equals(Turn)) showOutput(GameFrame.this, "It's not your turn");
                            else showOutput(GameFrame.this, "cannot control enemy's piece.");
                        }
                    }
                });

                pieceLabel.repaint();
                layeredPane.add(pieceLabel, Integer.valueOf(100));
                layeredPane.repaint();
            }

            x += TILE_WIDTH;
            if (x >= GAME_WIDTH) {
                x = 0;
                y += TILE_HEIGHT;
            }
        }

        layeredPane.repaint();
    }

    public void updateUIAfterMove(int row, int col, int destRow, int destCol) {
        Piece piece = (Piece) GameBoard.GameTiles[destRow][destCol];
        JLabel pieceLabel = getPieceLabelAt(row,col);
        if(pieceLabel!=null){
            pieceLabelMap.remove(new Point(col, row));
            pieceLabelMap.put(new Point(destCol,destRow), pieceLabel);
            pieceLabel.setLocation(computeLocation(destRow, destCol));
        }
        Set<Piece> adjacentPieces = new HashSet<>();
        if(piece instanceof canIncreaseOrDecreaseAdjacentPiecesPower) {
            adjacentPieces.addAll(getAdjacentPieces(row,col));
            adjacentPieces.addAll(getAdjacentPieces(destRow,destCol));
        }
        if(!adjacentPieces.isEmpty())
            for(Piece adjacentPiece : adjacentPieces)
                createPiecePowerLabel(adjacentPiece);
        createPiecePowerLabel(piece);
        repaint();
    }

    public void updateUIAfterAttack(int row, int col, int destRow, int destCol) {
        removePiece(destRow,destCol);
        updateUIAfterMove(row, col, destRow, destCol);
    }

    public void updateTurn(Side turn) {
        setTitle("Chess Crusader - You Are " + side.name() + " | Turn: " + turn);
    }

    private JLabel createSelectedPieceBackgroundLabel(int pieceCol, int pieceRow) {
        ImageIcon selectedPieceBgImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + SELECTED_PIECE_BACKGROUND_RESOURCE_PATH)));
        selectedPieceBgImage.setImage(selectedPieceBgImage.getImage().getScaledInstance(TILE_WIDTH, TILE_HEIGHT, 0));
        JLabel selectedPieceBgLabel = new JLabel(selectedPieceBgImage);
        selectedPieceBgLabel.setBounds(pieceCol * TILE_WIDTH, pieceRow * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        selectedPieceBgLabel.setOpaque(true);
        return selectedPieceBgLabel;
    }

    private JLabel createPieceMoveLabel(int col, int row) {
        ImageIcon pieceMoveImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + MOVE_CIRCLE_RESOURCE_PATH)));
        pieceMoveImage.setImage(pieceMoveImage.getImage().getScaledInstance(TILE_WIDTH / 2, TILE_HEIGHT / 2, 0));
        JLabel pieceMoveLabel = new JLabel(pieceMoveImage);
        pieceMoveLabel.setBounds(col * TILE_WIDTH, row * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        return pieceMoveLabel;
    }

    private JLabel createPieceLabel(Piece piece) {
        ImageIcon pieceImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + piece.getAssetName())));
        pieceImage.setImage(pieceImage.getImage().getScaledInstance(TILE_WIDTH - 10, TILE_HEIGHT - 10, Image.SCALE_SMOOTH));
        JLabel pieceLabel = new JLabel(pieceImage);
        pieceLabel.setBounds(piece.getX() * TILE_WIDTH, piece.getY() * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);


        return pieceLabel;
    }

    public void createPiecePowerLabel(Piece piece) {
        JLabel pieceLabel = getPieceLabelAt(piece.getY(),piece.getX());
        pieceLabel.removeAll();
        if (piece.getPower() < 0) {
            JLabel piecePowerLabel = new JLabel();
            piecePowerLabel.setText(String.valueOf(piece.getPower()));
            piecePowerLabel.setOpaque(true);
            piecePowerLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
            piecePowerLabel.setBounds(0, TILE_HEIGHT - 15, 15, 15);
            piecePowerLabel.setForeground(Color.WHITE);
            piecePowerLabel.setBackground(Color.RED);
            piecePowerLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
            pieceLabel.add(piecePowerLabel);
        } else {
            ImageIcon piecePowerImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + piece.getPowerAssetName())));
            piecePowerImage.setImage(piecePowerImage.getImage().getScaledInstance(TILE_WIDTH, TILE_HEIGHT, Image.SCALE_SMOOTH));
            JLabel piecePowerLabel = new JLabel(piecePowerImage);
            piecePowerLabel.setBounds(0, 0, TILE_WIDTH, TILE_HEIGHT);
            pieceLabel.add(piecePowerLabel, 0);
        }
    }

    public void clearMoveLabels() {
        if (!moveLabels.isEmpty()) {
            for (Map.Entry<Piece, List<JLabel>> p : moveLabels.entrySet()) {
                p.getKey().setSelected(false);
                for (JLabel l : p.getValue())
                    layeredPane.remove(l);
            }
            moveLabels.clear();
        }
        layeredPane.repaint();
    }

    public JLayeredPane getJLayeredPane(){
        return layeredPane;
    }
}
