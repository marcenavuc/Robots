package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class BarMenu {
    MainFrame mainFrame;

    public BarMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public JMenuBar generateMenuBar(AckFrame ackFrame) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createMainMenu(ackFrame));
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createMainMenu(AckFrame ackFrame) {
        return createMenu(mainFrame.getBundle().getString("bar.menu"), KeyEvent.VK_Q,
                mainFrame.getBundle().getString("bar.main.menu"),
                Arrays.asList(createItem(mainFrame.getBundle().getString("bar.main.exit"), 0, (event) -> {
            if (ackFrame.ackExit(mainFrame.getBundle().getString("bar.main.close"), mainFrame.getBundle()) == 0) {
                mainFrame.unregister();
                System.exit(0);
            }
        }), createItem(mainFrame.getBundle().getString("bar.main.change.lan"), 0, (event) -> {
                    String lan = ackFrame.ackLanguage("", mainFrame.languages, "bar.main.change.lan", mainFrame.getBundle());
                    Locale locale = Locale.forLanguageTag(lan);
                    mainFrame.changeLocale(locale);
                    })));

    }

    private JMenu createLookAndFeelMenu() {
        return createMenu(mainFrame.getBundle().getString("bar.display.name"), KeyEvent.VK_V,
                mainFrame.getBundle().getString("bar.display.manage"),
                Arrays.asList(createItem(mainFrame.getBundle().getString("bar.display.scheme.system"),
                        KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            mainFrame.invalidate();
        }), createItem(mainFrame.getBundle().getString("bar.display.scheme.universal"),
                        KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            mainFrame.invalidate();
        })));
    }

    private JMenu createTestMenu() {
        return createMenu(mainFrame.getBundle().getString("bar.test.name"),
                KeyEvent.VK_T, mainFrame.getBundle().getString("bar.test.description"),
                createItem(mainFrame.getBundle().getString("bar.test.text"),
                        KeyEvent.VK_S, (event) -> Logger.debug(mainFrame.getBundle().getString("bar.test.message"))));
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(mainFrame);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    private JMenuItem createItem(String text, int key, ActionListener actionListener) {
        JMenuItem itemMenu = new JMenuItem(text, key);
        itemMenu.addActionListener(actionListener);
        return itemMenu;
    }

    private JMenu createMenu(String text, int key, String textDescription, JMenuItem item) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(key);
        menu.getAccessibleContext().setAccessibleDescription(textDescription);
        if (item != null) { menu.add(item); }
        return menu;
    }

    private JMenu createMenu(String text, int key, String textDescription, List<JMenuItem> items) {
        JMenu menu = createMenu(text, key, textDescription, items.get(0));
        for (int i = 1; i < items.size(); i++) {
            menu.add(items.get(i));
        }
        return menu;
    }
}
