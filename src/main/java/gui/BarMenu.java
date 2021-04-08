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
        return createMenu("Меню", KeyEvent.VK_Q, "Главное меню",
                createItem("Выход", 0, (event) -> {
            if (ackFrame.ack("Закрыть окно?") == 0) {
                mainFrame.unregister();
                System.exit(0);
            }
        }));
    }

    private JMenu createLookAndFeelMenu() {
        return createMenu("Режим отображения", KeyEvent.VK_V,
                "Управление режимом отображения приложения",
                Arrays.asList(createItem("Системная схема",
                        KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            mainFrame.invalidate();
        }), createItem("Универсальная схема", KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            mainFrame.invalidate();
        })));
    }

    private JMenu createTestMenu() {
        return createMenu("Тесты",
                KeyEvent.VK_T, "Тестовые команды",
                createItem("Сообщение в лог",
                        KeyEvent.VK_S, (event) -> Logger.debug("Новая строка")));
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
