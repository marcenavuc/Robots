package gui;

import logic.*;
import logic.Robot;
import utils.MyMath;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.*;

import javax.swing.JPanel;


public class GameVisualizer extends JPanel {
    private final GameObserver gameObserver;

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameVisualizer(GameObserver gameObserver) {
        this.gameObserver = gameObserver;
        Timer timer = initTimer();
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
                int button = e.getButton();
                if (button == 1) {
                    Robot robot = new Robot(e.getX(), e.getY());
                    gameObserver.addRobot(robot);
                }
                if (button == 3) {
                    Food food = new Food(e.getX(), e.getY(), 1);
                    gameObserver.addFood(food);
                }
                repaint();
            }
        });
        MyMath.setSize(getWidth(), getHeight());
        setDoubleBuffered(true);
    }


    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        this.gameObserver.update();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        for (Robot robot: gameObserver.getRobots())
            drawRobot(g2d, robot);

        for (Food food: gameObserver.getFoods())
            drawFood(g2d, food);
    }

    private static void fillOval(Graphics g, int centerX, int centerY,
                                 int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY,
                                 int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, Robot robot) {
        int robotCenterX = robot.getRobotPosition().getKey();
        int robotCenterY = robot.getRobotPosition().getValue();
        AffineTransform t = AffineTransform.getRotateInstance(
                robot.getRobotDirection(), robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }

    private void drawFood(Graphics2D g, Food food) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, food.getPositionX(), food.getPositionY(), 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, food.getPositionX(), food.getPositionY(), 5, 5);
    }
}
