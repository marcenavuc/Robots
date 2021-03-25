package gui;

import gui.windows.*;
import log.Logger;
import logic.Robot;
import utils.Const;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import static gui.serialization.WindowSerializer.*;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 2L;
    public GameFrame gameFrame;
    public LogFrame logFrame;
    private final boolean notLoad;
    private final transient JDesktopPane desktopPane = new JDesktopPane();

    public MainFrame(boolean notLoad) throws PropertyVetoException {
        this.notLoad = notLoad;
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);
        setExtendedState(Frame.MAXIMIZED_BOTH);

        if (notLoad || loadWindowState(Const.mainFile, this) == null)
            setExtendedState(Frame.MAXIMIZED_BOTH);

        logFrame = createLogWindow();
        addWindow(logFrame);

        gameFrame = createGameWindow(new Robot(400, 400));
        addWindow(gameFrame);

        BarMenu barMenu = new BarMenu(this);
        setJMenuBar(barMenu.generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JFrame temp = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (getN(temp, new Object[] { "Да", "Нет" }) == 0) {
                    saveWindowState(gameFrame, Const.gameFile);
                    saveWindowState(logFrame, Const.logFile);
                    saveWindowState(temp, Const.mainFile);
                    System.exit(0);
                }
            }
        });
    }

    protected static int getN(Component frame, Object[] buttons) {
        return JOptionPane.showOptionDialog(frame,
                "Закрыть окно?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, buttons, buttons[1]);
    }

    protected static int getN(JFrame frame, Object[] buttons) {
        return JOptionPane.showOptionDialog(frame,
                "Закрыть окно?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, buttons, buttons[1]);
    }

    protected GameFrame createGameWindow(Robot robot) throws PropertyVetoException {
        GameFrame gameFrame = new GameFrame(robot);
        if (notLoad || loadWindowState(Const.gameFile, gameFrame) == null)
            gameFrame.setSize(400, 400);

        gameFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                super.internalFrameClosing(event);
                if (addOptionPane(event) == 0) {
                    saveWindowState(gameFrame, Const.gameFile);
                    gameFrame.dispose();
                }
            }
        });
        gameFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                robot.setSize(gameFrame.getWidth(), gameFrame.getHeight());
            }
        });
        return gameFrame;
    }

    protected LogFrame createLogWindow() throws PropertyVetoException {
        LogFrame logFrame = new LogFrame(Logger.getDefaultLogSource());
        if (notLoad || loadWindowState(Const.logFile, logFrame) == null) {
            logFrame.setLocation(10, 10);
            logFrame.setSize(300, 800);
            setMinimumSize(logFrame.getSize());
        }

        logFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                super.internalFrameClosing(event);
                if (addOptionPane(event) == 0) {
                    saveWindowState(logFrame, Const.logFile);
                    logFrame.exit();
                }
            }
        });

        logFrame.pack();
        Logger.debug("Протокол работает");
        return logFrame;
    }

    private int addOptionPane(InternalFrameEvent event) {
        if (getN(event.getInternalFrame(), new Object[] { "Да", "Нет" }) == 0) {
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
        for (Object frame : getFrames())
            if (frame instanceof LogFrame)
                ((LogFrame) frame).exit();
    }
}
