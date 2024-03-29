package gamelogic;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("RPG Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Game game = new Game();
        frame.setLayout(new BorderLayout());
        frame.add(game.getBoard(), BorderLayout.NORTH);
        frame.add(game.getInfoPanel(), BorderLayout.SOUTH);
        frame.addKeyListener(game);
        frame.pack();
    }
}