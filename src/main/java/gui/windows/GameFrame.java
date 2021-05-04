package gui.windows;

import gui.GameVisualizer;
import logic.GameObserver;
import logic.Core;

import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;

import static utils.Const.baseNameBundle;

public class GameFrame extends AbstractFrame {
    public GameFrame(GameObserver gameObserver, Locale locale) {
        super(ResourceBundle.getBundle(baseNameBundle, locale)
                .getString("frame.game"), true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new GameVisualizer(gameObserver), BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public void updateSize() {
        Core.setSize(getWidth(), getHeight());
    }

    public void changeLocale(Locale locale) {
        super.setTitle(ResourceBundle.getBundle(baseNameBundle, locale)
                .getString("frame.game"));
    }
}
