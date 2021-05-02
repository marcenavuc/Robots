package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class AckFrame extends JFrame {

    public AckFrame() {
        setVisible(false);
        setSize(0, 0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - 100, screenSize.height / 2 - 50);
    }

    public int ackExit(String text, ResourceBundle bundle) {
        setVisible(true);
        setSize(100, 50);
        Object[] buttons = new Object[] {
                bundle.getString("ack.yes"),
                bundle.getString("ack.no")
        };
        int answer = JOptionPane.showOptionDialog(this,
                text,
                bundle.getString("ack.accept"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, buttons, buttons[1]);
        setVisible(false);
        setSize(0, 0);
        return answer;
    }

    public String ackLanguage(String text, String[] languages, ResourceBundle bundle){
        setVisible(true);
        setSize(100, 50);
        String answer = (String)JOptionPane.showInputDialog(this, text,
                bundle.getString("bar.main.change.lan"),
                JOptionPane.QUESTION_MESSAGE, null,
                languages, languages[0]);
        setVisible(false);
        setSize(0, 0);
        return answer;
    }
}
