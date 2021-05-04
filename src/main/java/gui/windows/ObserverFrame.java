package gui.windows;

import logic.Observer;
import logic.Robot;
import utils.Tuple;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArraySet;

import static utils.Const.baseNameBundle;

public class ObserverFrame extends AbstractFrame implements Observer {
    private final JLabel label;
    private ResourceBundle bundle;
    private long currentRobot = -1;
    private final CopyOnWriteArraySet<Long> robotsId;
    private final JButton button;

    public ObserverFrame(Locale locale) {
        super(ResourceBundle.getBundle(baseNameBundle, locale)
                .getString("frame.coord.name"), true, true, true, true);
        robotsId = new CopyOnWriteArraySet<>();
        bundle = ResourceBundle.getBundle(baseNameBundle, locale);
        label = new JLabel(bundle.getString("frame.coord.add"), JLabel.LEFT);
        button = new JButton(bundle.getString("frame.coord.change.button"));
        button.addActionListener(actionEvent -> currentRobot = Long.parseLong((new AckFrame())
                .ackLanguage(
                        "", getIdRobots(),
                        "frame.coord.change.choose", bundle)));
        addPanel();
    }

    private void addPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(label);
        panel.add(button);
        add(panel);
    }

    @Override
    public void update(Tuple<Double, Double> m_robotPos,
                       double m_robotDirect,
                       Tuple<Integer, Integer> m_targetPos,
                       long id) {
        if (currentRobot == id)
            this.label.setText(bundle.getString("frame.coord.description") +
                "X:" + Math.round(m_robotPos.getKey()) + "  ---|---  Y:" + Math.round(m_robotPos.getValue()));
        else
            if (currentRobot == -1) {
                currentRobot = id;
                this.label.setText(bundle.getString("frame.coord.description") +
                        "X:" + Math.round(m_robotPos.getKey()) + "  ---|---  Y:" + Math.round(m_robotPos.getValue()));
            }
    }

    public void changeLocale(Locale locale) {
        bundle = ResourceBundle.getBundle(baseNameBundle, locale);
        super.setTitle(bundle.getString("frame.coord.name"));
        label.setText(bundle.getString("frame.coord.add"));
        button.setText(bundle.getString("frame.coord.change.button"));
    }

    public void addRobot(Robot robot) {
        robotsId.add(robot.getId());
        if (currentRobot == -1)
            currentRobot = robot.getId();
        robot.registerObserver(this);
    }

    public void delRobot(long id) {
        robotsId.remove(id);
        if (currentRobot == id)
            currentRobot = -1;
    }

    public String[] getIdRobots() {
        String[] temp = new String[robotsId.size()];
        int i = 0;
        for (Long id : robotsId) {
            if (i >= temp.length)
                break;
            temp[i++] = id.toString();
        }
        return temp;
    }
}