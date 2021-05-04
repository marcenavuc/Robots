package gui.windows;

import logic.GameObserver;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.Const.baseNameBundle;

public class RobotSettingsFrame extends JInternalFrame {
    private final JLabel[] labels;
    private final JTextField[] texts;
    private final JButton button;

    public RobotSettingsFrame(GameObserver gameObserver, Locale locale) {
        super("", true, true, true, true);
        labels = new JLabel[] {
                new JLabel("", JLabel.LEFT),
                new JLabel("", JLabel.LEFT),
                new JLabel("", JLabel.LEFT)
        };
        texts = new JTextField[3];
        JPanel panel = new JPanel(new GridLayout(4, 2));
        for (int i = 0; i < labels.length; i++) {
            panel.add(labels[i]);
            texts[i] = new JTextField(10);
            panel.add(texts[i]);
        }
        button = new JButton("");
        button.addActionListener(actionEvent -> gameObserver.setConstForRobots(
                Double.parseDouble(texts[0].getText()),
                Double.parseDouble(texts[1].getText()),
                Long.parseLong(texts[2].getText())));
        panel.add(button);
        changeLocale(locale);
        getContentPane().add(panel);
    }

    public void changeLocale(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(baseNameBundle, locale);
        super.setTitle(bundle.getString("frame.setting.robot.name"));
        labels[0].setText(bundle.getString("frame.setting.robot.velocity"));
        labels[1].setText(bundle.getString("frame.setting.robot.angular"));
        labels[2].setText(bundle.getString("frame.setting.robot.live"));
        button.setText(ResourceBundle.getBundle(baseNameBundle, locale)
                .getString("frame.setting.robot.accept"));
    }
}
