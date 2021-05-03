package utils;

import logic.Food;
import logic.Robot;

public class Core {
    public static int widthField;
    public static int heightField;

    public static double findDistance(Robot robot, Food food) {
        double distance = findDistance(robot.getRobotPosition().getKey(),
                                       robot.getRobotPosition().getValue(),
                                       food.getPositionX(),
                                       food.getPositionY());
        double angle = angleTo(robot.getRobotPosition().getKey(),
                               robot.getRobotPosition().getValue(),
                               food.getPositionX(),
                               food.getPositionY());
        return angle * distance;
    }

    public static double findDistance(double x1, double y1,
                                   double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public static double asNormalizedRadians(double angle) {
        while (angle < 0)
            angle += 2 * Math.PI;

        while (angle >= 2 * Math.PI)
            angle -= 2 * Math.PI;

        return angle;
    }

    public static double angleTo(double fromX, double fromY,
                                  double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    public static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;

        return Math.min(value, max);
    }

    public static double getNewCoordinates(Tuple<Double, Double> robotPosition,
                                     double robotDirection,
                                     double velocity,
                                     double angularVelocity,
                                     double duration, boolean x) {
        if (x) {
            double newX = robotPosition.getKey() + velocity / angularVelocity *
                    (Math.sin(robotDirection  + angularVelocity * duration) -
                            Math.sin(robotDirection));
            if (!Double.isFinite(newX))
                newX = robotPosition.getKey()
                        + velocity * duration * Math.cos(robotDirection);

            return newX;
        }
        double newY = robotPosition.getValue() - velocity / angularVelocity *
                (Math.cos(robotDirection  + angularVelocity * duration) -
                        Math.cos(robotDirection));
        if (!Double.isFinite(newY))
            newY = robotPosition.getValue()
                    + velocity * duration * Math.sin(robotDirection);

        return newY;
    }

    public static Tuple<Tuple<Double, Double>, Double> calculateNewCords(Robot robot,
                                         double velocity,
                                         double angularVelocity,
                                         double duration) {
        double robotDirection = robot.getRobotDirection();
        velocity = applyLimits(velocity, 0, robot.MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity,
                -robot.MAX_ANGULAR_VELOCITY, robot.MAX_ANGULAR_VELOCITY);

        double newX = getNewCoordinates(robot.getRobotPositionD(), robotDirection,
                velocity, angularVelocity, duration, true);
        double newY = getNewCoordinates(robot.getRobotPositionD(), robotDirection,
                velocity, angularVelocity, duration, false);
        if (newX> widthField || newX < 0 || newY > heightField || newY < 0) {
            double wallAngle = 0;
            if (newX > widthField || newX < 0)
                wallAngle = Math.PI / 2;

            robotDirection = wallAngle * 2 - robotDirection;
            newX = getNewCoordinates(robot.getRobotPositionD(), robotDirection,
                    velocity, angularVelocity, duration, true);
            newY = getNewCoordinates(robot.getRobotPositionD(), robotDirection,
                    velocity, angularVelocity, duration, false);
        } else
            robotDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);
        return new Tuple<>(new Tuple<>(newX, newY), robotDirection);
    }

    public static void moveRobot(Robot robot,
                           double velocity,
                           double angularVelocity,
                           double duration) {

        Tuple<Tuple<Double, Double>, Double> args = calculateNewCords(robot,
                velocity, angularVelocity, duration);

        double newX = args.getKey().getKey();
        double newY = args.getKey().getValue();
        double robotDirection = args.getValue();

        robot.setRobotPosition(newX, newY);
        robot.setRobotDirection(robotDirection);
    }

    public static void setSize(int width, int height) {
        widthField = width;
        heightField = height;
    }

    public static int round(double value) {
        return (int)(value + 0.5);
    }
}
