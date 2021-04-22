package gui;

import gui.windows.*;
import log.Logger;
import logic.GameObserver;
import utils.Const;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import static serialization.Serializer.*;

public class MainFrame extends JFrame {
    private final boolean notLoad;
    private final transient JDesktopPane desktopPane = new JDesktopPane();

    public GameFrame gameFrame;
    public LogFrame logFrame;
    public GameObserver gameObserver;
    AckFrame ackFrame;

    public MainFrame(boolean notLoad) throws PropertyVetoException {
        ackFrame = new AckFrame();
        this.notLoad = notLoad;
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        if (notLoad || loadWindowState(Const.mainFile, this) == null)
            setExtendedState(Frame.MAXIMIZED_BOTH);

        gameObserver = new GameObserver(400, 400);

        InternalFrameAdapter adapter = new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                super.internalFrameClosing(event);
                if (addOptionPane(event) == 0) {
                    saveWindowState(logFrame, Const.logFile);
                    logFrame.exit();
                }
            }
        };

        logFrame = createLogWindow();
        logFrame.addInternalFrameListener(adapter);
        addWindow(logFrame);

        gameFrame = createGameWindow(gameObserver);
        gameFrame.addInternalFrameListener(adapter);
        addWindow(gameFrame);

        BarMenu barMenu = new BarMenu(this);
        setJMenuBar(barMenu.generateMenuBar(ackFrame));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JFrame temp = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ackFrame.ack("Закрыть окно?") == 0) {
                    saveWindowState(gameFrame, Const.gameFile);
                    saveWindowState(logFrame, Const.logFile);
                    saveWindowState(temp, Const.mainFile);
                    System.exit(0);
                }
            }
        });
        pack();
        setVisible(true);
    }

    protected GameFrame createGameWindow(GameObserver gameObserver) throws PropertyVetoException {
        GameFrame gameFrame = new GameFrame(gameObserver);
        if (notLoad || loadWindowState(Const.gameFile, gameFrame) == null)
            gameFrame.setSize(400, 400);

        gameFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gameObserver.updateSize(gameFrame.getWidth(), gameFrame.getHeight());
            }
        });
        return gameFrame;
    }

    protected LogFrame createLogWindow() throws PropertyVetoException {
        LogFrame logFrame = new LogFrame(Logger.getDefaultLogSource());
        if (notLoad || loadWindowState(Const.logFile, logFrame) == null) {
            logFrame.setLocation(10, 10);
            logFrame.m_logContent.setSize(300, 800);
        }

        logFrame.pack();
        Logger.debug("Протокол работает");
        return logFrame;
    }

    private int addOptionPane(InternalFrameEvent event) {
        if (ackFrame.ack("Закрыть окно?") == 0) {
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
