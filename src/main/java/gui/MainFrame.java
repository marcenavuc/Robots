package gui;

import gui.windows.*;
import log.Logger;
import logic.GameObserver;
import serialization.Serializer;
import utils.Const;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.Locale;
import java.util.ResourceBundle;

import static serialization.Serializer.*;
import static utils.Const.baseNameBundle;

public class MainFrame extends JFrame {
    private final boolean notLoad;
    private final transient JDesktopPane desktopPane = new JDesktopPane();
    protected ResourceBundle bundle;
    public GameFrame gameFrame;
    public LogFrame logFrame;
    public ObserverFrame observerFrame;
    public GameObserver gameObserver;
    public RobotSettingsFrame robotSettingsFrame;
    public final String[] languages;
    AckFrame ackFrame;

    public MainFrame(boolean notLoad, String[] languages) throws PropertyVetoException {
        bundle = ResourceBundle.getBundle(baseNameBundle, Locale.getDefault());
        this.languages = languages;
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

        observerFrame = createObserverWindow();
        observerFrame.addInternalFrameListener(adapter);
        addWindow(observerFrame);
        gameObserver = new GameObserver(observerFrame);

        logFrame = createLogWindow();
        logFrame.addInternalFrameListener(adapter);
        addWindow(logFrame);

        gameFrame = createGameWindow(gameObserver);
        gameFrame.addInternalFrameListener(adapter);
        addWindow(gameFrame);

        robotSettingsFrame = createRobotsSettings(gameObserver);
        robotSettingsFrame.addInternalFrameListener(adapter);
        addWindow(robotSettingsFrame);

        BarMenu barMenu = new BarMenu(this);
        setJMenuBar(barMenu.generateMenuBar(ackFrame));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        MainFrame temp = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ackFrame.ackExit(bundle.getString("bar.main.close"), bundle) == 0) {
                    saveWindowState(gameFrame, Const.gameFile);
                    saveWindowState(logFrame, Const.logFile);
                    saveWindowState(observerFrame, Const.observerFile);
                    saveWindowState(robotSettingsFrame, Const.robotSettingsFile);
                    Serializer.saveWindowState(temp, Const.mainFile);
                    System.exit(0);
                }
            }
        });
        pack();
        setVisible(true);
    }

    protected GameFrame createGameWindow(GameObserver gameObserver) throws PropertyVetoException {
        GameFrame gameFrame = new GameFrame(gameObserver, bundle.getLocale());
        if (notLoad || loadWindowState(Const.gameFile, gameFrame) == null)
            gameFrame.setSize(400, 400);

        gameFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gameObserver.updateSize(gameFrame.getWidth(), gameFrame.getHeight());
            }
        });
        gameFrame.updateSize();
        return gameFrame;
    }

    protected LogFrame createLogWindow() throws PropertyVetoException {
        LogFrame logFrame = new LogFrame(Logger.getDefaultLogSource(), bundle.getLocale());
        if (notLoad || loadWindowState(Const.logFile, logFrame) == null) {
            logFrame.setLocation(10, 10);
            logFrame.m_logContent.setSize(300, 800);
        }

        logFrame.pack();
        Logger.debug(bundle.getString("log.message"));
        return logFrame;
    }

    protected ObserverFrame createObserverWindow() throws PropertyVetoException {
        ObserverFrame observerFrame = new ObserverFrame(bundle.getLocale());
        if (notLoad || loadWindowState(Const.observerFile, observerFrame) == null) {
            observerFrame.setSize(200, 100);
        }
        return observerFrame;
    }

    protected RobotSettingsFrame createRobotsSettings(GameObserver gameObserver) throws PropertyVetoException {
        RobotSettingsFrame frame = new RobotSettingsFrame(gameObserver, bundle.getLocale());
        if (notLoad || loadWindowState(Const.robotSettingsFile, frame) == null) {
            frame.setSize(300, 400);
        }
        return frame;
    }

    private int addOptionPane(InternalFrameEvent event) {
        if (ackFrame.ackExit(bundle.getString("bar.main.close"), bundle) == 0) {
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

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void changeLocale(Locale locale) {
        bundle = ResourceBundle.getBundle(baseNameBundle, locale);
        BarMenu barMenu = new BarMenu(this);
        setJMenuBar(barMenu.generateMenuBar(ackFrame));
        logFrame.changeLocale(locale);
        gameFrame.changeLocale(locale);
        observerFrame.changeLocale(locale);
        robotSettingsFrame.changeLocale(locale);
    }

    public void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle(baseNameBundle, locale);
    }
}
