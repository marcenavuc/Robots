package gui;

import javax.swing.*;
import java.awt.*;

public class AckFrame extends JFrame{
    private Object[] buttons = new Object[] { "Да", "Нет" };

    public AckFrame(){
        setVisible(false);
        setSize(0, 0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - 100, screenSize.height / 2 - 50);
    }

    public int ack(String text) {
        setVisible(true);
        setSize(100, 50);
        int answer = JOptionPane.showOptionDialog(this,
                text,
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, buttons, buttons[1]);
        setVisible(false);
        setSize(0, 0);
        return answer;
    }
}
