package gui.windows;

import gui.GameVisualizer;
import logic.GameObserver;

import java.awt.*;
import javax.swing.*;

public class GameFrame extends JInternalFrame {
    public GameFrame(GameObserver gameObserver) {
        super("Игровое поле", true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new GameVisualizer(gameObserver), BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
