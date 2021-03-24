package gui;

import log.Logger;
import utils.Robot;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;

public class MainApplicationFrame extends JFrame implements Serializable {
    private transient JDesktopPane desktopPane = new JDesktopPane();
    public GameWindow gameWindow = null;
    public LogWindow logWindow = null;
    public Robot robot = null;

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = createGameWindow();
        addWindow(gameWindow);

        BarMenu barMenu = new BarMenu(this);
        setJMenuBar(barMenu.generateMenuBar());

        try {
            deserialize();
        } catch (IOException e) {
            // we don't found file
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // i don't know what we should do
            // maybe nothing or send to him that we're stupid
            e.printStackTrace();
        }
    }

    protected GameWindow createGameWindow(Robot robot, int width, int height) {
        GameWindow gameWindow = new GameWindow(robot);
        gameWindow.setSize(width, height);
        gameWindow.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event){
                super.internalFrameClosing(event);
                addOptionPane(event);
                try {
                    serialize();
                } catch (IOException e) {
                    // do nothing, or send to user message that we can't save his session
                    e.printStackTrace();
                }
            }
        });
        gameWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                robot.setSize(gameWindow.getWidth(), gameWindow.getHeight());
                double robotPositionX = robot.getRobotPositionX();
                double robotPositionY = robot.getRobotPositionY();
                if (robotPositionX > gameWindow.getWidth()) {
                    robotPositionX = gameWindow.getWidth();
                }
                if (robotPositionY > gameWindow.getHeight()) {
                    robotPositionY = gameWindow.getHeight();
                }
                robot.setRobotPosition(robotPositionX, robotPositionY);
            }
        });
        return gameWindow;
    }

    protected GameWindow createGameWindow() {
        return createGameWindow(new Robot(400, 400), 400, 400);
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                super.internalFrameClosing(event);
                if (addOptionPane(event) == 0)
                    logWindow.exit();
            }
        });
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private int addOptionPane(InternalFrameEvent event) {
        if (getN(event.getInternalFrame(),
                new Object[] {"Да", "Нет"}) == 0) {
            event.getInternalFrame().setVisible(false);
            return 0;
        }
        event.getInternalFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        return 1;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void unregister() {
        for(Object frame : getFrames())
            if (frame instanceof LogWindow)
                ((LogWindow)frame).exit();
    }

    protected static int getN(Component frame, Object[] buttons) {
        return JOptionPane
                .showOptionDialog(frame, "Закрыть окно?",
                        "Подтверждение", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, buttons,
                        buttons[1]);
    }

    void serialize() throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream("save.out"));
        objectOutputStream.writeObject(robot);
        objectOutputStream.writeObject(gameWindow.getSize());
        objectOutputStream.writeObject(gameWindow.getLocation());
        objectOutputStream.close();
    }

    void deserialize() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream("save.out"));
        robot = (Robot) objectInputStream.readObject();
        Dimension size = (Dimension) objectInputStream.readObject();
        Point location = (Point) objectInputStream.readObject();
        if (gameWindow != null)
            desktopPane.remove(gameWindow);
        gameWindow = createGameWindow(robot, size.width, size.height);
        gameWindow.setLocation(location.x, location.y);
        addWindow(gameWindow);
        objectInputStream.close();
    }
}
