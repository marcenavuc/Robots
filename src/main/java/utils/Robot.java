package utils;

import java.awt.Point;
import java.util.ArrayList;

public class Robot {
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;

    private volatile double width;
    private volatile double height;

    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    public Robot(int width, int height){
        setSize(width, height);
    }

    public void setTargetPosition(Point point) {
        targetPositionX = point.x;
        targetPositionY = point.y;
    }

    public void setRobotPosition(double x, double y) {
        this.robotPositionX = x;
        this.robotPositionY = y;
    }

    private static double distance(double x1, double y1,
                                   double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY,
                                  double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }
    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;

        return Math.min(value, max);
    }

    private double getNewCoordinates(double velocity, double angularVelocity,
                                     double duration, boolean x) {
        if (x) {
            double newX = robotPositionX + velocity / angularVelocity *
                    (Math.sin(robotDirection  + angularVelocity * duration) -
                            Math.sin(robotDirection));
            if (!Double.isFinite(newX))
                newX = robotPositionX + velocity * duration * Math.cos(robotDirection);

            return newX;
        }
        double newY = robotPositionY - velocity / angularVelocity *
                (Math.cos(robotDirection  + angularVelocity * duration) -
                        Math.cos(robotDirection));
        if (!Double.isFinite(newY))
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);

        return newY;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        double newX = getNewCoordinates(velocity, angularVelocity, duration, true);
        double newY = getNewCoordinates(velocity, angularVelocity, duration, false);
        if (newX> width || newX < 0 || newY > height - 5 || newY < 0) {
            double wallAngle = 0;
            if (newX > width || newX < 0)
                wallAngle = Math.PI / 2;

            robotDirection = wallAngle * 2 - robotDirection;
            newX = getNewCoordinates(velocity, angularVelocity, duration, true);
            newY = getNewCoordinates(velocity, angularVelocity, duration, false);
        } else
            robotDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);
        setRobotPosition(newX, newY);
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0)
            angle += 2 * Math.PI;

        while (angle >= 2 * Math.PI)
            angle -= 2 * Math.PI;

        return angle;
    }

    public void update() {
        if (distance(targetPositionX, targetPositionY,
                robotPositionX, robotPositionY) < 0.5)
            return;

        double angleToTarget = angleTo(robotPositionX, robotPositionY,
                targetPositionX, targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > robotDirection)
            angularVelocity = MAX_ANGULAR_VELOCITY;

        if (angleToTarget < robotDirection)
            angularVelocity = -MAX_ANGULAR_VELOCITY;

        moveRobot(MAX_VELOCITY, angularVelocity, 10);
    }
    public int getRobotPositionX() {
        return round(this.robotPositionX);
    }

    public int getRobotPositionY() {
        return round(this.robotPositionY);
    }

    public double getDRobotPositionX() {
        return this.robotPositionX;
    }

    public double getDRobotPositionY() {
        return this.robotPositionY;
    }

    public double getRobotDirection() {
        return this.robotDirection;
    }

    public int getTargetPositionX() {
        return this.targetPositionX;
    }

    public int getTargetPositionY() {
        return this.targetPositionY;
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}