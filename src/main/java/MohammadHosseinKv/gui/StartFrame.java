package main.java.MohammadHosseinKv.gui;

import main.java.MohammadHosseinKv.controller.GameController;
import main.java.MohammadHosseinKv.model.Side;
import main.java.MohammadHosseinKv.network.SocketManager;

import static main.java.MohammadHosseinKv.util.Util.*;
import static main.java.MohammadHosseinKv.util.Constants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class StartFrame extends JFrame {

    public StartFrame() {
        super("Welcome to Chess Crusader");
        setLayout(null);
        getContentPane().setPreferredSize(new Dimension(FRAME_DIMENSION.width, FRAME_DIMENSION.height));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        ImageIcon Backgroundimage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(START_BACKGROUND_RESOURCE_PATH)));
        Backgroundimage.setImage(Backgroundimage.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH));

        ImageIcon TitleImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(START_TITLE_RESOURCE_PATH)));
        TitleImage.setImage(TitleImage.getImage().getScaledInstance((int) (getWidth() / 1.2), getHeight() / 3, Image.SCALE_SMOOTH));

        JLabel FrameBackground = new JLabel(Backgroundimage);
        FrameBackground.setBounds(0, 0, getWidth(), getHeight());

        JLabel GameTitle = new JLabel(TitleImage);
        GameTitle.setBounds((getWidth() / 3) - (getWidth() / 4), (getHeight() / 40), (int) (getWidth() / 1.2), getHeight() / 3);

        JButton CreateGame = createButton("Create Game", -120, (getHeight() / 2) - 100, 150, 50, Color.WHITE, Color.BLACK, Color.BLACK);
        CreateGame.addActionListener(this::handleCreateGameButtonAction);
        JButton JoinGame = createButton("Join Game", -120, getHeight() / 2, 150, 50, Color.WHITE, Color.BLACK, Color.BLACK);
        JoinGame.addActionListener(this::handleJoinGameButtonAction);

        add(CreateGame);
        add(JoinGame);
        add(GameTitle);
        add(FrameBackground);
        centralizeFrame(this);
        setVisible(true);
    }

    private JButton createButton(String buttonText, int x, int y, int width, int height, Color backgroundColor, Color foregroundColor, Color borderColor) {
        JButton button = new JButton(buttonText);
        button.setBounds(x, y, width, height);
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setBorder(BorderFactory.createLineBorder(borderColor));
        button.setFocusable(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                handleMouseEnterAndExitToButton(e, button, x + 120, y, width, height, Color.WHITE, Color.BLACK, Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                handleMouseEnterAndExitToButton(e, button, x, y, width, height, Color.WHITE, Color.BLACK, Color.BLACK);
            }
        });

        return button;
    }

    private void handleMouseEnterAndExitToButton(MouseEvent e, JButton button, int x, int y, int width, int height, Color backgroundColor, Color borderColor, Color foregroundColor) {
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createLineBorder(borderColor));
        button.setForeground(foregroundColor);
        button.setBounds(x, y, width, height);
        button.repaint();
    }

    private void handleJoinGameButtonAction(ActionEvent e) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            this.dispose();
            new GameController(Side.BLACK, new SocketManager(socket));
        } catch (IOException ex) {
            ex.printStackTrace();
            showOutput(this, ex.getMessage());
        }
    }

    private void handleCreateGameButtonAction(ActionEvent e) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket socket = serverSocket.accept();
            this.dispose();
            new GameController(Side.WHITE, new SocketManager(socket));
        } catch (IOException ex) {
            ex.printStackTrace();
            showOutput(this, ex.getMessage());
        }
    }

}
