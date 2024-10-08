package main.java.MohammadHosseinKv.gui;

import main.java.MohammadHosseinKv.controller.GameController;
import main.java.MohammadHosseinKv.logic.*;
import main.java.MohammadHosseinKv.model.*;

import static main.java.MohammadHosseinKv.util.Constants.*;
import static main.java.MohammadHosseinKv.util.Util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GameFrame extends JFrame {

    // Layers index
    private static final Integer GAME_BOARD_LAYER = 10;
    private static final Integer SELECT_PIECE_BACKGROUND_LAYER = 20;
    private static final Integer PIECE_LAYER = 100;
    private static final Integer PIECE_MOVEABLE_LAYER = 200;
    private GameController controller;
    private JLayeredPane layeredPane;
    private Side Turn;
    private final Side side;
    private Map<Piece, List<JLabel>> moveLabels = Collections.synchronizedMap(new HashMap<>());
    private Map<Point, JLabel> pieceLabelMap = Collections.synchronizedMap(new HashMap<>());
    private final int TILE_WIDTH = TILE_DIMENSION.width;
    private final int TILE_HEIGHT = TILE_DIMENSION.height;

    public GameFrame(Side side, GameController controller, Side Turn) {
        super();
        this.side = side;
        this.controller = controller;
        this.Turn = Turn;
        setTitle("Chess Crusader - You Are " + side + " | Turn: " + Turn);
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

        centralizeFrame(this);
    }

    public JLabel getPieceLabelAt(int row, int col) {
        return pieceLabelMap.get(new Point(col, row));
    }

    public void removePieceFromBoard(JLabel pieceLabel) {
        layeredPane.remove(pieceLabel);
        layeredPane.repaint();
        pieceLabelMap.values().remove(pieceLabel);
    }

    public void addPieceToBoard(int row, int col, JLabel pieceLabel) {
        Point piecePosition = new Point(col, row);
        pieceLabelMap.put(piecePosition, pieceLabel);
        pieceLabel.setBounds(col * TILE_WIDTH, row * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        layeredPane.add(pieceLabel, PIECE_LAYER);
        layeredPane.repaint();
    }

    public void removePiece(int row, int col) {
        Point piecePosition = new Point(col, row);
        JLabel pieceLabel = pieceLabelMap.get(piecePosition);
        if (pieceLabel != null) {
            removePieceFromBoard(pieceLabel);
            pieceLabelMap.remove(piecePosition);
        }
    }

    public void initUI() {
        int GAME_WIDTH = GAME_DIMENSION.width;
        int GAME_HEIGHT = GAME_DIMENSION.height;

        // add 8x8 gameboard background to layeredpane
        ImageIcon backGroundImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(GAME_BOARD_BACKGROUND_RESOURCE_PATH)));
        backGroundImage.setImage(backGroundImage.getImage().getScaledInstance(GAME_WIDTH, GAME_HEIGHT, Image.SCALE_AREA_AVERAGING));
        JLabel backGroundLabel = new JLabel(backGroundImage);
        backGroundLabel.setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
        layeredPane.add(backGroundLabel, GAME_BOARD_LAYER);

        // Fill screen with piece labels
        int x = 0;
        int y = 0;
        for (int i = 0; i < NUMBER_OF_TILES; i++) {
            int row = y / TILE_HEIGHT;
            int col = x / TILE_WIDTH;
            if (GameBoard.GameTiles[row][col] != null && GameBoard.GameTiles[row][col] instanceof Piece) {
                Piece piece = (Piece) GameBoard.GameTiles[row][col];
                JLabel pieceLabel = createPieceLabel(piece);
                addPieceToBoard(row, col, pieceLabel);
                createPiecePowerLabel(piece);
                pieceLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handlePieceSelection(e, piece);
                    }
                });

                pieceLabel.repaint();
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

    private void handlePieceSelection(MouseEvent e, Piece piece) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (side.equals(Turn) && piece.getSide().equals(side)) {
                if (piece.isSelected()) {
                    piece.setSelected(false);
                    clearMoveLabels();
                } else {
                    piece.setSelected(true);
                    clearMoveLabels();
                    createSelectedPieceMoveLabels(e, piece);
                }
            } else if (!side.equals(Turn)) showOutput(GameFrame.this, "It's not your turn");
            else showOutput(GameFrame.this, "cannot control enemy's piece.");
        }
    }

    private void createSelectedPieceMoveLabels(MouseEvent e, Piece piece) {
        int pieceCol = piece.getX();
        int pieceRow = piece.getY();
        JLabel selectedPieceBgLabel = createSelectedPieceBackgroundLabel(pieceCol, pieceRow);
        List<JLabel> selectedPieceMoveLabels = new ArrayList<>();
        selectedPieceMoveLabels.add(selectedPieceBgLabel);
        layeredPane.add(selectedPieceBgLabel, SELECT_PIECE_BACKGROUND_LAYER);

        Integer[][] pieceMoveDirections = piece.getMoveDirections();
        for (int i = 0; i < pieceMoveDirections.length; i++) {
            int moveLabelCol = pieceMoveDirections[i][0];
            int moveLabelRow = pieceMoveDirections[i][1];
            JLabel pieceMoveLabel = createPieceMoveLabel(moveLabelCol, moveLabelRow);
            selectedPieceMoveLabels.add(pieceMoveLabel);
            layeredPane.add(pieceMoveLabel, PIECE_MOVEABLE_LAYER);
            layeredPane.repaint();
            pieceMoveLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        handlePieceAction(e, piece, pieceRow, pieceCol, moveLabelRow, moveLabelCol);
                    }
                }
            });
        }
        moveLabels.put(piece, selectedPieceMoveLabels);
    }

    private void handlePieceAction(MouseEvent e, Piece piece, int curRow, int curCol, int destRow, int destCol) {
        piece.setSelected(false);
        if (GameBoard.GameTiles[destRow][destCol] == null) {
            controller.movePiece(curRow, curCol, destRow, destCol, true);
        } else if (GameBoard.GameTiles[destRow][destCol] instanceof Piece) {
            controller.attackPiece(curRow, curCol, destRow, destCol, true);
        }
        clearMoveLabels();
    }

    public void updateUIAfterMove(int row, int col, int destRow, int destCol) {
        Piece piece = (Piece) GameBoard.GameTiles[destRow][destCol];
        JLabel pieceLabel = getPieceLabelAt(row, col);
        if (pieceLabel != null) {
            pieceLabelMap.remove(new Point(col, row));
            pieceLabelMap.put(new Point(destCol, destRow), pieceLabel);
            pieceLabel.setBounds(destCol * TILE_WIDTH, destRow * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        }
        Set<Piece> adjacentPieces = new HashSet<>();
        if (piece instanceof canAdjustAdjacentPiecesPower) {
            adjacentPieces.addAll(getAdjacentPieces(row, col));
            adjacentPieces.addAll(getAdjacentPieces(destRow, destCol));
        }
        if (!adjacentPieces.isEmpty())
            for (Piece adjacentPiece : adjacentPieces)
                createPiecePowerLabel(adjacentPiece);
        createPiecePowerLabel(piece);
        repaint();
    }

    public void updateUIAfterAttack(int row, int col, int destRow, int destCol) {
        removePiece(destRow, destCol);
        updateUIAfterMove(row, col, destRow, destCol);
    }

    public void updateTurn(Side turn) {
        this.Turn = turn;
        setTitle("Chess Crusader - You Are " + side + " | Turn: " + turn);
    }

    private JLabel createSelectedPieceBackgroundLabel(int pieceCol, int pieceRow) {
        ImageIcon selectedPieceBgImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(SELECTED_PIECE_BACKGROUND_RESOURCE_PATH)));
        selectedPieceBgImage.setImage(selectedPieceBgImage.getImage().getScaledInstance(TILE_WIDTH, TILE_HEIGHT, Image.SCALE_SMOOTH));
        JLabel selectedPieceBgLabel = new JLabel(selectedPieceBgImage);
        selectedPieceBgLabel.setBounds(pieceCol * TILE_WIDTH, pieceRow * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        selectedPieceBgLabel.setOpaque(true);
        return selectedPieceBgLabel;
    }

    private JLabel createPieceMoveLabel(int col, int row) {
        ImageIcon pieceMoveImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(MOVE_CIRCLE_RESOURCE_PATH)));
        pieceMoveImage.setImage(pieceMoveImage.getImage().getScaledInstance(TILE_WIDTH / 2, TILE_HEIGHT / 2, Image.SCALE_SMOOTH));
        JLabel pieceMoveLabel = new JLabel(pieceMoveImage);
        pieceMoveLabel.setBounds(col * TILE_WIDTH, row * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        return pieceMoveLabel;
    }

    private JLabel createPieceLabel(Piece piece) {
        ImageIcon pieceImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(piece.getAssetResourcePath())));
        pieceImage.setImage(pieceImage.getImage().getScaledInstance(TILE_WIDTH - 10, TILE_HEIGHT - 10, Image.SCALE_SMOOTH));
        return new JLabel(pieceImage);
    }

    public void createPiecePowerLabel(Piece piece) {
        JLabel pieceLabel = getPieceLabelAt(piece.getY(), piece.getX());
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
            pieceLabel.add(piecePowerLabel, 0);
        } else {
            ImageIcon piecePowerImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(piece.getPowerAssetResourcePath())));
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
}
