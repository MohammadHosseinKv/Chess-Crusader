import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class Main extends JFrame implements Gameboard {

    Thread RequestListener;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    Side side;
    Side Turn = Side.WHITE;
    Map<Piece, List<JLabel>> moveLabels = new HashMap<>();
    JLayeredPane layeredPane;

    public static void main(String[] args) {
        JFrame StartFrame = new JFrame("Welcome to Chess Crusader");
        StartFrame.setLayout(null);
        StartFrame.getContentPane().setPreferredSize(new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT));
        StartFrame.pack();
        StartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StartFrame.setResizable(false);

        ImageIcon Backgroundimage = new ImageIcon(Constants.START_BACKGROUND_ASSET_NAME);
        Backgroundimage.setImage(Backgroundimage.getImage().getScaledInstance(StartFrame.getWidth(), StartFrame.getHeight(), 0));

        ImageIcon TitleImage = new ImageIcon(Constants.START_TITLE_ASSET_NAME);
        TitleImage.setImage(TitleImage.getImage().getScaledInstance((int) (StartFrame.getWidth() / 1.2), StartFrame.getHeight() / 3, Image.SCALE_SMOOTH));

        JLabel FrameBackground = new JLabel(Backgroundimage);
        FrameBackground.setBounds(0, 0, StartFrame.getWidth(), StartFrame.getHeight());

        JLabel GameTitle = new JLabel(TitleImage);
        GameTitle.setBounds((StartFrame.getWidth() / 3) - (StartFrame.getWidth() / 4), (StartFrame.getHeight() / 40), (int) (StartFrame.getWidth() / 1.2), StartFrame.getHeight() / 3);


        JButton Creategame = new JButton("Create Game");
        Creategame.setBounds(-120, ((StartFrame.getHeight() / 2) - 100), 150, 50);
        Creategame.setForeground(Color.BLACK);
        Creategame.setBackground(Color.WHITE);
        Creategame.setFocusable(false);
        Creategame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Creategame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == Creategame) {
                    try {
                        ServerSocket serverSocket = new ServerSocket(8080);
                        Socket socket = serverSocket.accept();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                        StartFrame.dispose();
                        new Main(Side.WHITE, socket, objectOutputStream, objectInputStream);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        Creategame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Creategame.setBackground(Color.BLACK);
                Creategame.setBorder(BorderFactory.createLineBorder(Color.RED));
                Creategame.setForeground(Color.WHITE);
                Creategame.setBounds(0, ((StartFrame.getHeight() / 2) - 100), 150, 50);
                Creategame.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Creategame.setBackground(Color.WHITE);
                Creategame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                Creategame.setForeground(Color.BLACK);
                Creategame.setBounds(-120, ((StartFrame.getHeight() / 2) - 100), 150, 50);
                Creategame.repaint();
            }
        });

        JButton Joingame = new JButton("Join Game");
        Joingame.setBounds(-120, ((StartFrame.getHeight() / 2)), 150, 50);
        Joingame.setForeground(Color.BLACK);
        Joingame.setBackground(Color.WHITE);
        Joingame.setFocusable(false);
        Joingame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Joingame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == Joingame) {
                    try {
                        Socket socket = new Socket("localhost", 8080);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                        StartFrame.dispose();
                        new Main(Side.BLACK, socket, objectOutputStream, objectInputStream);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        Joingame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Joingame.setBackground(Color.BLACK);
                Joingame.setBorder(BorderFactory.createLineBorder(Color.RED));
                Joingame.setForeground(Color.WHITE);
                Joingame.setBounds(0, ((StartFrame.getHeight() / 2)), 150, 50);
                Joingame.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Joingame.setBackground(Color.WHITE);
                Joingame.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                Joingame.setForeground(Color.BLACK);
                Joingame.setBounds(-120, ((StartFrame.getHeight() / 2)), 150, 50);
                Joingame.repaint();
            }

        });


        StartFrame.add(Creategame);
        StartFrame.add(Joingame);
        StartFrame.add(GameTitle);
        StartFrame.add(FrameBackground);
        StartFrame.setVisible(true);
    }


    Main(Side side, Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {

        super();
        setTitle("Chess Crusader - You Are " + side.name() + " | Turn: " + Turn.name());
        setResizable(false);
        getContentPane().setPreferredSize(new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, Constants.LAYEREDPANE_WIDTH, Constants.LAYEREDPANE_HEIGHT);
        layeredPane.setOpaque(true);
        layeredPane.setVisible(true);
        add(layeredPane);
        addRequestListener(socket, objectOutputStream, objectInputStream);

        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.side = side;

        // add 8x8 gameboard background to layeredpane
        ImageIcon backGroundImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + Constants.GAMEBOARD8x8_BACKGROUND_ASSET_NAME)));
        backGroundImage.setImage(backGroundImage.getImage().getScaledInstance(Constants.LAYEREDPANE_WIDTH, Constants.LAYEREDPANE_HEIGHT, Image.SCALE_AREA_AVERAGING));
        JLabel backGroundLabel = new JLabel(backGroundImage);
        backGroundLabel.setBounds(0, 0, Constants.LAYEREDPANE_WIDTH, Constants.LAYEREDPANE_HEIGHT);
        layeredPane.add(backGroundLabel, Integer.valueOf(10));

        initGameboardPieces();
        // Fill screen with piece labels
        int x = 0;
        int y = 0;
        for (int i = 0; i < Constants.NUMBER_OF_TILES; i++) {

            int row = y / Constants.TILE_HEIGHT;
            int col = x / Constants.TILE_WIDTH;

            if (GameTiles[row][col] != null && GameTiles[row][col] instanceof Piece piece) {

                JLabel pieceLabel = createPieceLabel(piece);
                piece.setPieceLabel(pieceLabel);
                calculatePower(piece);
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
                                    List<JLabel> moveLabels = new ArrayList<>();
                                    moveLabels.add(selectedPieceBgLabel);
                                    layeredPane.add(selectedPieceBgLabel, Integer.valueOf(20));

                                    Integer[][] pieceMoveDirections = piece.getMoveDirections(Main.this);
                                    for (int i = 0; i < pieceMoveDirections.length; i++) {
                                        int moveLabelCol = pieceMoveDirections[i][0];
                                        int moveLabelRow = pieceMoveDirections[i][1];
                                        JLabel pieceMoveLabel = createPieceMoveLabel(moveLabelCol, moveLabelRow);
                                        moveLabels.add(pieceMoveLabel);
                                        layeredPane.add(pieceMoveLabel, Integer.valueOf(200));
                                        layeredPane.repaint();
                                        pieceMoveLabel.addMouseListener(new MouseAdapter() {
                                            @Override
                                            public void mouseClicked(MouseEvent e) {
                                                int destRow = pieceMoveLabel.getY() / Constants.TILE_HEIGHT;
                                                int destCol = pieceMoveLabel.getX() / Constants.TILE_WIDTH;
                                                if (GameTiles[destRow][destCol] == null) {
                                                    piece.setSelected(false);
                                                    movePiece(pieceRow, pieceCol, destRow, destCol);
                                                    clearMoveLabels();
                                                    changeTurn();
                                                    sendRequest("movePiece", pieceRow, pieceCol, destRow, destCol);
                                                    sendRequest("changeTurn");
                                                } else {
                                                    piece.setSelected(false);
                                                    attackPiece(pieceRow, pieceCol, destRow, destCol);
                                                    clearMoveLabels();
                                                    changeTurn();
                                                    sendRequest("attackPiece", pieceRow, pieceCol, destRow, destCol);
                                                    sendRequest("changeTurn");
                                                }
                                            }
                                        });
                                    }
                                    Main.this.moveLabels.put(piece, moveLabels);

                                }
                            } else if (!side.equals(Turn)) JOptionPane.showMessageDialog(null, "It's not your turn");
                            else JOptionPane.showMessageDialog(null, "cannot control enemy's piece.");
                        }
                    }
                });

                pieceLabel.repaint();
                layeredPane.add(pieceLabel, Integer.valueOf(100));
                layeredPane.repaint();
            }

            x += Constants.TILE_WIDTH;
            if (x >= Constants.LAYEREDPANE_WIDTH) {
                x = 0;
                y += Constants.TILE_HEIGHT;
            }
        }

        layeredPane.repaint();
    }

    private JLabel createSelectedPieceBackgroundLabel(int pieceCol, int pieceRow) {
        ImageIcon selectedPieceBgImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + Constants.SELECTED_PIECE_BACKGROUND_ASSET_NAME)));
        selectedPieceBgImage.setImage(selectedPieceBgImage.getImage().getScaledInstance(Constants.TILE_WIDTH, Constants.TILE_HEIGHT, 0));
        JLabel selectedPieceBgLabel = new JLabel(selectedPieceBgImage);
        selectedPieceBgLabel.setBounds(pieceCol * Constants.TILE_WIDTH, pieceRow * Constants.TILE_HEIGHT, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
        selectedPieceBgLabel.setOpaque(true);
        return selectedPieceBgLabel;
    }

    private JLabel createPieceMoveLabel(int col, int row) {
        ImageIcon pieceMoveImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + Constants.MOVE_CIRCLE_ASSET_NAME)));
        pieceMoveImage.setImage(pieceMoveImage.getImage().getScaledInstance(Constants.TILE_WIDTH / 2, Constants.TILE_HEIGHT / 2, 0));
        JLabel pieceMoveLabel = new JLabel(pieceMoveImage);
        pieceMoveLabel.setBounds(col * Constants.TILE_WIDTH, row * Constants.TILE_HEIGHT, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
        return pieceMoveLabel;
    }

    private JLabel createPieceLabel(Piece piece) {
        ImageIcon pieceImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + piece.getAssetName())));
        pieceImage.setImage(pieceImage.getImage().getScaledInstance(Constants.TILE_WIDTH - 10, Constants.TILE_HEIGHT - 10, Image.SCALE_SMOOTH));
        JLabel pieceLabel = new JLabel(pieceImage);
        pieceLabel.setBounds(piece.getX() * Constants.TILE_WIDTH, piece.getY() * Constants.TILE_HEIGHT, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);


        return pieceLabel;
    }

    private void createPiecePowerLabel(Piece piece) {
        JLabel pieceLabel = piece.getPieceLabel();
        pieceLabel.removeAll();
        if (piece.getPower() < 0) {
            JLabel piecePowerLabel = new JLabel();
            piecePowerLabel.setText(String.valueOf(piece.getPower()));
            piecePowerLabel.setOpaque(true);
            piecePowerLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
            piecePowerLabel.setBounds(0, Constants.TILE_HEIGHT - 15, 15, 15);
            piecePowerLabel.setForeground(Color.WHITE);
            piecePowerLabel.setBackground(Color.RED);
            piecePowerLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
            pieceLabel.add(piecePowerLabel);
        } else {
            ImageIcon piecePowerImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + piece.getPowerAssetName())));
            piecePowerImage.setImage(piecePowerImage.getImage().getScaledInstance(Constants.TILE_WIDTH, Constants.TILE_HEIGHT, Image.SCALE_SMOOTH));
            JLabel piecePowerLabel = new JLabel(piecePowerImage);
            piecePowerLabel.setBounds(0, 0, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
            pieceLabel.add(piecePowerLabel, 0);
        }
    }

    private void calculatePower(Piece piece) {
        piece.resetPower();
        int row = piece.getY();
        int col = piece.getX();
        for (int j = 0; j < Constants.ADJACENT_DIRECTIONS.length; j++) {
            try {
                int dirX = Constants.ADJACENT_DIRECTIONS[j][0];
                int dirY = Constants.ADJACENT_DIRECTIONS[j][1];
                if (GameTiles[row + dirY][col + dirX] != null && GameTiles[row + dirY][col + dirX] instanceof canIncreaseOrDecreaseAdjacentPiecesPower pieceThatCanIncreaseOrDecreaseAdjacentPiecesPower) {
                    pieceThatCanIncreaseOrDecreaseAdjacentPiecesPower.increaseOrDecreaseAdjacentPiecesPower(this, piece);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                // ignore
            }
        }
        createPiecePowerLabel(piece);
    }


    @Override
    public void initGameboardPieces() {
        // initialize white pieces
        GameTiles[0][0] = new Archer(0, 0, Side.WHITE);
        GameTiles[0][1] = new Archer(1, 0, Side.WHITE);
        GameTiles[0][2] = new Knight(2, 0, Side.WHITE);
        GameTiles[0][3] = new Castle(3, 0, Side.WHITE);
        GameTiles[0][4] = new Assassin(4, 0, Side.WHITE);
        GameTiles[0][5] = new Knight(5, 0, Side.WHITE);
        GameTiles[0][6] = new Archer(6, 0, Side.WHITE);
        GameTiles[0][7] = new Archer(7, 0, Side.WHITE);
        for (int i = 0; i < GameTiles[1].length; i++)
            GameTiles[1][i] = new Soldier(i, 1, Side.WHITE);

        // initialize black pieces
        GameTiles[7][0] = new Archer(0, 7, Side.BLACK);
        GameTiles[7][1] = new Archer(1, 7, Side.BLACK);
        GameTiles[7][2] = new Knight(2, 7, Side.BLACK);
        GameTiles[7][3] = new Assassin(3, 7, Side.BLACK);
        GameTiles[7][4] = new Castle(4, 7, Side.BLACK);
        GameTiles[7][5] = new Knight(5, 7, Side.BLACK);
        GameTiles[7][6] = new Archer(6, 7, Side.BLACK);
        GameTiles[7][7] = new Archer(7, 7, Side.BLACK);
        for (int i = 0; i < GameTiles[6].length; i++)
            GameTiles[6][i] = new Soldier(i, 6, Side.BLACK);

    }

    @Override
    public void movePiece(int row, int col, int destRow, int destCol) {
        Piece piece = ((Piece) GameTiles[row][col]);
        JLabel pieceLabel = piece.getPieceLabel();
        pieceLabel.setBounds(destCol * Constants.TILE_WIDTH, destRow * Constants.TILE_HEIGHT, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
        pieceLabel.removeAll();
        Set<Piece> adjacentPieces = null;
        if (piece instanceof canIncreaseOrDecreaseAdjacentPiecesPower) {
            adjacentPieces = new HashSet<>();
            for (int i = 0; i < Constants.ADJACENT_DIRECTIONS.length; i++) {
                int dirX = Constants.ADJACENT_DIRECTIONS[i][0];
                int dirY = Constants.ADJACENT_DIRECTIONS[i][1];
                try {
                    if (GameTiles[row + dirY][col + dirX] != null && GameTiles[row + dirY][col + dirX] instanceof Piece adjacentPiece) {
                        adjacentPieces.add(adjacentPiece);
                    }
                    if (GameTiles[destRow + dirY][destCol + dirX] != null && GameTiles[destRow + dirY][destCol + dirX] instanceof Piece adjacentPiece) {
                        if (!adjacentPiece.equals(piece))
                            adjacentPieces.add(adjacentPiece);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // ignored
                }
            }
        }

        piece.setX(destCol);
        piece.setY(destRow);
        GameTiles[destRow][destCol] = piece;
        GameTiles[row][col] = null;
        if (adjacentPieces != null && !adjacentPieces.isEmpty()) {
            for (Piece adjP : adjacentPieces)
                calculatePower(adjP);
        }
        calculatePower(piece);
        layeredPane.repaint();

    }

    @Override
    public void attackPiece(int row, int col, int destRow, int destCol) {
        Piece piece = ((Piece) GameTiles[row][col]);
        Piece targetPiece = ((Piece) GameTiles[destRow][destCol]);
        layeredPane.remove(targetPiece.getPieceLabel());
        GameTiles[destRow][destCol] = null;
        movePiece(row, col, destRow, destCol);
        if (targetPiece instanceof Castle) {
            clearMoveLabels();
            sendRequest("Gameover");
            JOptionPane.showMessageDialog(null, piece.getSide() + " Won the Game.");
            System.exit(0);
        }
    }

    private void clearMoveLabels() {
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

    private synchronized void addRequestListener(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        if (RequestListener == null) {
            RequestListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (socket.isConnected()) {
                        try {
                            String Request = (String) objectInputStream.readObject();
                            switch (Request.toUpperCase()) {
                                case "MOVEPIECE":
                                    int row = (Integer) objectInputStream.readObject();
                                    int col = (Integer) objectInputStream.readObject();
                                    int destRow = (Integer) objectInputStream.readObject();
                                    int destCol = (Integer) objectInputStream.readObject();
                                    movePiece(row, col, destRow, destCol);
                                    break;
                                case "ATTACKPIECE":
                                    row = (Integer) objectInputStream.readObject();
                                    col = (Integer) objectInputStream.readObject();
                                    destRow = (Integer) objectInputStream.readObject();
                                    destCol = (Integer) objectInputStream.readObject();
                                    attackPiece(row, col, destRow, destCol);
                                    break;
                                case "CHANGETURN":
                                    changeTurn();
                                    break;
                                case "GAMEOVER":
                                    JOptionPane.showMessageDialog(null, "Game Over , you lost.");
                                    System.exit(0);
                                default:
                                    JOptionPane.showMessageDialog(null, "Invalid Request:  " + Request);
                                    break;
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            try {
                                socket.close();
                                e.printStackTrace();
                                System.exit(0);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
            RequestListener.start();
        }
    }

    @Override
    public void changeTurn() {
        String lastTurn = Turn.name();
        Turn = Turn.equals(Side.WHITE) ? Side.BLACK : Side.WHITE;
        int lastIndexOfTurn = getTitle().lastIndexOf(lastTurn);
        setTitle(getTitle().substring(0, lastIndexOfTurn) + getTitle().substring(lastIndexOfTurn).replace(lastTurn, Turn.name()));
    }

    private void sendRequest(Object... args) {
        try {
            for (Object arg : args)
                objectOutputStream.writeObject(arg);

            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}