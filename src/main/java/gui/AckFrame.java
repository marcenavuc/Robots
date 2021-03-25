package gui;

import javax.swing.*;
import java.awt.*;

public class AckFrame extends JFrame{
    private Object[] buttons = new Object[] { "Да", "Нет" };
    public AckFrame(){
        setSize(100, 50);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - 100, screenSize.height / 2 - 50);
        setVisible(true);
    }

    public int ack() {
        return JOptionPane.showOptionDialog(this,
                "Загрузить последнее состояние окна?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, buttons, buttons[1]);
    }
}
