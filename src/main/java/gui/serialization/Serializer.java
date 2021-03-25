package gui.serialization;

import gui.MainFrame;
import gui.windows.LogFrame;

import java.beans.PropertyVetoException;
import java.io.*;

import javax.swing.*;

public class Serializer {
    public static void saveWindowState(JInternalFrame frame, String name) {
        Info info = new Info();
        info.height = frame.getHeight();
        info.width = frame.getWidth();
        info.xPosition = frame.getX();
        info.yPosition = frame.getY();
        info.isMaximized = frame.isMaximum();
        info.isMinimized = frame.isIcon();
        serialize(info, name);
    }

    public static void saveWindowState(JFrame frame, String name) {
        Info info = new Info();
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

    private static Info deserialize(String name) {
        Info restored = null;
        try (InputStream is = new FileInputStream(name);
             ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is))) {
            restored = (Info) ois.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return restored;
    }

    public static JInternalFrame loadWindowState(String name, JInternalFrame frame) throws PropertyVetoException {
        Info info = (Info) deserialize(name);
        if (info != null) {
            if (frame instanceof LogFrame){
                frame.setLocation(info.xPosition, info.yPosition);
                ((LogFrame) frame).m_logContent.setSize(info.width, info.height);
                frame.setIcon(info.isMinimized);
                frame.setMaximum(info.isMaximized);
            }
            frame.setLocation(info.xPosition, info.yPosition);
            frame.setSize(info.width, info.height);
            frame.setIcon(info.isMinimized);
            frame.setMaximum(info.isMaximized);
        }
        return info != null ? frame : null;
    }

    public static JFrame loadWindowState(String name, JFrame frame) {
        Info info = deserialize(name);
        if (info != null) {
            if (frame instanceof MainFrame){
                frame.setLocation(info.xPosition, info.yPosition);
                frame.setSize(info.width, info.height);
            }
        }
        return info != null ? frame : null;
    }
}
