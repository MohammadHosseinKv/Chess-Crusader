import gui.StartFrame;
import javax.swing.*;

public class Main extends JFrame {

    public static void main(String[] args) {
        new Main();
    }

    Main(){
        SwingUtilities.invokeLater(StartFrame::new);
    }




}