package gui;

import utils.Robot;

import java.awt.*;
import java.io.Serializable;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow(Robot robot)
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(robot);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }


}
