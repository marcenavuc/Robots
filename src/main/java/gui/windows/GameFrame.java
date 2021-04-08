package gui;

import logic.GameObserver;
import logic.Robot;

import java.awt.*;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow(GameObserver gameObserver)
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(gameObserver);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }


}
