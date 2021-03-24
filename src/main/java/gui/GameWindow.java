package gui;
import logic.Robot;
import java.awt.*;
import java.io.*;
import javax.swing.*;

public class GameWindow extends JInternalFrame implements Externalizable
{
    private GameVisualizer m_visualizer;
    public GameWindow(Robot robot)
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(robot);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public GameWindow() {
        new GameWindow(null);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(m_visualizer);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_visualizer = (GameVisualizer) in.readObject();
    }
}
