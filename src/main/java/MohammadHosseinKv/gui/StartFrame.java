package main.java.MohammadHosseinKv.gui;

import main.java.MohammadHosseinKv.controller.GameController;
import main.java.MohammadHosseinKv.network.SocketManager;

import static main.java.MohammadHosseinKv.model.Side.*;
import static main.java.MohammadHosseinKv.util.Util.*;
import static main.java.MohammadHosseinKv.util.Constants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

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

        JButton CreateGame = createButton("Create Game", (getWidth() / 2) - (150 / 2), ((getHeight() / 2) - 100) - (50 / 2),
                150, 50, Color.WHITE, Color.BLACK, Color.RED);
        CreateGame.addActionListener(this::handleCreateGameButtonAction);
        JButton JoinGame = createButton("Join Game", (getWidth() / 2) - (150 / 2), (getHeight() / 2) - (50 / 2),
                150, 50, Color.WHITE, Color.BLACK, Color.RED);
        JoinGame.addActionListener(this::handleJoinGameButtonAction);
        JButton GameDocument = createButton("Game Document", (getWidth() / 2) - (150 / 2), ((getHeight() / 2) + 100) - (50 / 2),
                150, 50, Color.WHITE, Color.BLACK, Color.RED);
        GameDocument.addActionListener(this::handleGameDocumentButtonAction);
        JButton GitHubRepo = createButton("GitHub Repository", (getWidth() / 2) - (150 / 2), ((getHeight() / 2) + 200) - (50 / 2),
                150, 50, Color.WHITE, Color.BLACK, Color.RED);
        GitHubRepo.addActionListener(this::handleGitHubRepoButtonAction);

        add(CreateGame);
        add(JoinGame);
        add(GameDocument);
        add(GitHubRepo);
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
                handleMouseEnterAndExitToButton(e, button, foregroundColor, borderColor, backgroundColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                handleMouseEnterAndExitToButton(e, button, backgroundColor, borderColor, foregroundColor);
            }
        });

        return button;
    }

    private void handleMouseEnterAndExitToButton(MouseEvent e, JButton button, Color backgroundColor, Color borderColor, Color foregroundColor) {
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createLineBorder(borderColor));
        button.setForeground(foregroundColor);
        button.repaint();
    }

    private void handleJoinGameButtonAction(ActionEvent e) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            this.dispose();
            new GameController(BLACK, new SocketManager(socket));
        } catch (IOException ex) {
            ex.printStackTrace();
            showOutput(this, ex.getMessage());
        }
    }

    private void handleCreateGameButtonAction(ActionEvent e) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket socket = serverSocket.accept();
            this.dispose();
            new GameController(WHITE, new SocketManager(socket));
        } catch (IOException ex) {
            ex.printStackTrace();
            showOutput(this, ex.getMessage());
        }
    }

    private void handleGameDocumentButtonAction(ActionEvent actionEvent) {
        if (Desktop.isDesktopSupported()) {
            try {
                File gameDocument = new File(GAME_DOCUMENT_PDF_FILE_PATH);
                if (gameDocument.exists()) {
                    Desktop.getDesktop().open(gameDocument);
                } else {
                    showOutput(this, "Document File Doesn't Exist in " + System.getProperty("user.dir") + "\\" + GAME_DOCUMENT_PDF_FILE_PATH);
                }
            } catch (IOException e) {
                e.printStackTrace();
                showOutput(this, e.getMessage());
            }
        } else {
            showOutput(this, "OS Desktop is not supported for JDK.");
        }
    }

    private void handleGitHubRepoButtonAction(ActionEvent actionEvent) {
        String htmlLink = "<html>" +
                "Click the link below to redirect to GitHub repository: <br>" +
                "<a href='" + GITHUB_REPOSITORY + "'>" + GITHUB_REPOSITORY + "</a>" +
                "</html>";
        JEditorPane editorPane = new JEditorPane("text/html", htmlLink);
        editorPane.setFont(new Font("Arial", Font.PLAIN, 25));
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.addHyperlinkListener(e -> {
            try {
                onHyperLinkClick(e, new URI(GITHUB_REPOSITORY), this);
            } catch (URISyntaxException ex) {
                showOutput(this, ex.getMessage());
            }
        });
        showOutput(this, editorPane);
    }


}
