package gui.serialization;

import gui.serialization.WindowInfo;

import java.beans.PropertyVetoException;
import java.io.*;

import javax.swing.*;

public class WindowSerializer {
    public static void saveWindowState(JInternalFrame frame, String name) {
        WindowInfo info = new WindowInfo();
        info.height = frame.getHeight();
        info.width = frame.getWidth();
        info.xPosition = frame.getX();
        info.yPosition = frame.getY();
        info.isMaximized = frame.isMaximum();
        info.isMinimized = frame.isIcon();
        serialize(info, name);
    }

    public static void saveWindowState(JFrame frame, String name) {
        WindowInfo info = new WindowInfo();
        info.height = frame.getHeight();
        info.width = frame.getWidth();
        info.xPosition = frame.getX();
        info.yPosition = frame.getY();
        serialize(info, name);
    }

    private static void serialize(Object info, String name) {
        File file = new File(name);
        try (OutputStream os = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os))) {
            oos.writeObject(info);
            oos.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static WindowInfo deserialize(String name) {
        WindowInfo restored = null;
        try (InputStream is = new FileInputStream(name);
             ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is))) {
            restored = (WindowInfo) ois.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return restored;
    }

    public static JInternalFrame loadWindowState(String name, JInternalFrame frame) throws PropertyVetoException {
        WindowInfo info = (WindowInfo) deserialize(name);
        if (info != null) {
            frame.setLocation(info.xPosition, info.yPosition);
            frame.setSize(info.width, info.height);
            frame.setIcon(info.isMinimized);
            frame.setMaximum(info.isMaximized);
        }
        return info != null ? frame : null;
    }

    public static JFrame loadWindowState(String name, JFrame frame) {
        WindowInfo info = (WindowInfo) deserialize(name);
        if (info != null) {
            frame.setLocation(info.xPosition, info.yPosition);
            frame.setSize(info.width, info.height);
        }
        return info != null ? frame : null;
    }
}
