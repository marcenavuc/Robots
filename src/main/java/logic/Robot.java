package logic;

import java.util.Timer;
import java.util.TimerTask;

public class Robot {
    private final Long id;
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;
    private GameObserver gameObserver;

    private volatile double widthField;
    private volatile double heightField;

    private volatile Food food;
    private volatile double hunger = 0;
    private volatile double hungerLoss = 0;

    private final double MAX_VELOCITY = 0.1;
    private final double MAX_ANGULAR_VELOCITY = 0.001;
    private final long MAX_LIVE_WITHOUT_FOOD = 1000 * 5;

    private Timer timer;

    public volatile boolean haveFood = false;

    public Robot(double robotPositionX, double robotPositionY,
                 int widthField, int heightField, Long id) {
        setRobotPosition(robotPositionX, robotPositionY);
        setSize(widthField, heightField);
        this.id = id;
        initTimer();
    }

    public Robot(int width, int height, Long id){
        setSize(width, height);
        this.id = id;
        initTimer();
    }

    private void initTimer(){
         timer = new Timer();
         timer.schedule(new CheckAliveRobot(), MAX_LIVE_WITHOUT_FOOD);
    }

    public void addObserver(GameObserver gameObserver){
        this.gameObserver = gameObserver;
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
        if (newX> widthField || newX < 0 || newY > heightField || newY < 0) {
            double wallAngle = 0;
            if (newX > widthField || newX < 0)
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

    private boolean canEat() {
        return food != null && distance(food.getPositionX(), food.getPositionY(),
                robotPositionX, robotPositionY) < 0.5;
    }

    private void eat() {
        hunger += food.getSatiety();
        haveFood = false;
    }

    public void update() {
        if (food == null)
            return;

        hunger -= hungerLoss;
        if (haveFood & food != null & canEat()) {
            eat();
            return;
        }

        double angleToTarget = angleTo(robotPositionX, robotPositionY,
                food.getPositionX(), food.getPositionY());
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

    public Food getFood() {
        return food;
    }

//    public int getFoodPositionX() {
//        return this.food.getPositionX();
//    }
//
//    public int getFoodPositionY() {
//        return this.food.getPositionY();
//    }

    public double getHunger() {
        return hunger;
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    public void setSize(int width, int height) {
        this.widthField = width;
        this.heightField = height;
    }

    public void setHunger(double hunger) {
        this.hunger = hunger;
    }

    public void setFood(Food food) {
        haveFood = true;
        this.food = food;
    }

    public void delFood(){
        food = null;
    }

    protected class CheckAliveRobot extends TimerTask {
        public void run() {
            if (hunger <= 0 && gameObserver != null) {
                gameObserver.getMapRobots().remove(id);
                timer.cancel();
            }
            else
                if(hunger > 0 && gameObserver != null)
                    hunger =- 1;
        }
    }
}
