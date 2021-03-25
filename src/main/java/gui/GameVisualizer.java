package gui;

import logic.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Timer;
import java.util.TimerTask;

public class GameVisualizer extends JPanel implements Externalizable {
    private Timer timer = initTimer();
    private Robot robot;

    public GameVisualizer(Robot robot) {
        this.robot = robot;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                robot.setTargetPosition(e.getPoint());
                repaint();
            }
        });

        setDoubleBuffered(true);
    }

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        this.robot.update();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, robot.getRobotPositionX(), robot.getRobotPositionY(), robot.getRobotDirection());
        drawTarget(g2d, robot.getTargetPositionX(), robot.getTargetPositionY());
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = robot.getRobotPositionX();
        int robotCenterY = robot.getRobotPositionY();

        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(robot);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        robot = (Robot) in.readObject();
        timer = initTimer();
    }
}
