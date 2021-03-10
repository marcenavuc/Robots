package gui;

import log.Logger;
import utils.Robots;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = createGameWindow(new Robots());
        addWindow(gameWindow);

        BarMenu barMenu = new BarMenu(this);
        setJMenuBar(barMenu.generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    protected GameWindow createGameWindow(Robots robot) {
        GameWindow gameWindow = new GameWindow(robot);
        gameWindow.setSize(400, 400);
        gameWindow.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent event){
                super.internalFrameClosing(event);
                addOptionPane(event);
            }
        });
        return gameWindow;
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
                logWindow.exit();
                addOptionPane(event);
            }
        });
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private void addOptionPane(InternalFrameEvent event) {
        if (getN(event.getInternalFrame(),
                new Object[] {"Да", "Нет"}) == 0)
            event.getInternalFrame().setVisible(false);
        event.getInternalFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
}
