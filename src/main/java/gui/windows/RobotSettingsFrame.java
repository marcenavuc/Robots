package gui.windows;

import logic.GameObserver;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static utils.Const.baseNameBundle;

public class RobotSettingsFrame extends AbstractFrame {
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
                parseStringToDouble(texts[0].getText()),
                parseStringToDouble(texts[1].getText()),
                parseStringToLong(texts[2].getText())));
        panel.add(button);
        changeLocale(locale);
        getContentPane().add(panel);
    }

    public void changeLocale(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(baseNameBundle, locale);
        super.setTitle(bundle.getString("frame.setting.robot.name"));
        int i = 0;
        for (String key : Arrays.asList("frame.setting.robot.velocity",
                "frame.setting.robot.angular", "frame.setting.robot.live"))
            labels[i++].setText(bundle.getString(key));

        button.setText(ResourceBundle.getBundle(baseNameBundle, locale)
                .getString("frame.setting.robot.accept"));
    }

    private static double parseStringToDouble(String value) {
        return value == null || value.equals("") ? 0.0d : Double.parseDouble(value);
    }

    private static long parseStringToLong(String value) {
        return value == null || value.equals("") ? 0 : Long.parseLong(value);
    }
}
