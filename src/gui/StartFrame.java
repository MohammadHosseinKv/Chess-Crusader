package gui;

import model.Side;

import static util.Util.*;
import static util.Constants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class StartFrame extends JFrame {

    public StartFrame() {
        super("Welcome to Chess Crusader");
        setLayout(null);
        getContentPane().setPreferredSize(new Dimension(FRAME_DIMENSION.width, FRAME_DIMENSION.height));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        ImageIcon Backgroundimage = new ImageIcon(START_BACKGROUND_RESOURCE_PATH);
        Backgroundimage.setImage(Backgroundimage.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH));

        ImageIcon TitleImage = new ImageIcon(START_TITLE_RESOURCE_PATH);
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
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.dispose();
            new GameFrame(Side.BLACK, socket, objectOutputStream, objectInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
            showOutput(this, ex.getMessage());
        }
    }

    private void handleCreateGameButtonAction(ActionEvent e) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            Socket socket = serverSocket.accept();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.dispose();
          new GameFrame(Side.WHITE, socket, objectOutputStream, objectInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
            showOutput(this, ex.getMessage());
        }
    }

}
