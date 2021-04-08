package gui.windows;

import gui.GameVisualizer;
import logic.Robot;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JInternalFrame {
    private final transient GameVisualizer m_visualizer;

    public GameFrame(Robot robot) {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(robot);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
    }
}
