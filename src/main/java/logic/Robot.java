package logic;

import utils.Position;
import utils.Tuple;


import java.util.Timer;
import java.util.TimerTask;

import static logic.Core.*;

public class Robot /*implements Runnable*/ {
    private volatile Position robotPosition;
    private volatile double robotDirection = 0;
    private GameObserver gameObserver;

    private volatile Position targetPosition;
    private double satiety = 0;
    private volatile int hunger = 0;

    public final double MAX_VELOCITY = 0.1;
    public final double MAX_ANGULAR_VELOCITY = 0.001;
    private final long MAX_LIVE_WITHOUT_FOOD = 1000 * 5;

    public boolean isAlive;
//    private final Thread thread;
    private Timer timer;


    public Robot(double robotPositionX, double robotPositionY) {
        setRobotPosition(robotPositionX, robotPositionY);
//        thread = new Thread(this);
//        thread.setDaemon(true);
        isAlive = true;
        initTimer();
    }

//    public void start() {
//        thread.start();
//    }
//
//    public void interrupt() {
//        thread.interrupt();
//    }
//
//    public boolean checkStart() {
//        return thread.isAlive();
//    }

    public void addObserver(GameObserver gameObserver) {
        this.gameObserver = gameObserver;
    }

    public void setRobotPosition(double x, double y) {
        this.robotPosition = new Position(x, y);
    }

    private boolean canEat() {
        return targetPosition != null && findDistance(targetPosition.getX(), targetPosition.getY(),
                robotPosition.getX(), robotPosition.getY()) < 0.5;
    }

    public void eat() {
        synchronized (this) {
            hunger += 1;
        }
        satiety = 0;
    }

    public void update() {
        if (satiety == 0 & getTarget() != null) {
            gameObserver.deleteFoodFromMap(targetPosition);
            targetPosition = null;
        }
        if (satiety == 0 & gameObserver.getFoods().size() > 0) {
            Food nearestFood = gameObserver.findClosedFoodToRobot(this);
            gameObserver.attachFoodToRobot(this, nearestFood);
        }

        if (targetPosition == null) {
            satiety = 0;
            return;
        }

        if (satiety != 0 & canEat()) {
            eat();
            return;
        }

        double angleToTarget = angleTo(robotPosition.getX(), robotPosition.getY(),
                targetPosition.getX(), targetPosition.getY());
        double angularVelocity = 0;
        if (angleToTarget > robotDirection)
            angularVelocity = MAX_ANGULAR_VELOCITY;

        if (angleToTarget < robotDirection)
            angularVelocity = -MAX_ANGULAR_VELOCITY;

        moveRobot(this, MAX_VELOCITY, angularVelocity, 10.0);
    }

    public Position getRobotPosition() {
        return new Position(round(this.robotPosition.getX()), round(this.robotPosition.getY()));
    }

    public Position getRobotPositionD() {
        return this.robotPosition;
    }

    public void setRobotDirection(double value) {
        this.robotDirection = value;
    }

    public double getRobotDirection() {
        return this.robotDirection;
    }

    public Position getTarget() {
        return targetPosition;
    }

    public void vichHunger() {
        synchronized (this) {
            hunger -= 1;
        }
    }

    public int getHunger() {
        return hunger;
    }

    public void setFood(Food food) {
        if (food != null) {
            satiety = food.getSatiety();
            this.targetPosition = food.getPosition();
        }
    }

    public void delFood() {
        targetPosition = null;
        satiety = 0;
    }
//
//    @Override
//    public void run() {
//        while (isAlive)
//            update();
//    }

    private class CheckAliveRobot extends TimerTask {
        public void run() {
            //            for (long id : robots.keySet()) {
            //                Robot robot = robots.get(id);
            if (getHunger() <= 0) {
                isAlive = false;
                //getMapRobots().remove(id);
                timer.cancel();
            } else
                vichHunger();
        }
    }

    private void initTimer(){
        timer = new Timer("robots_timer", true);
        timer.scheduleAtFixedRate (new CheckAliveRobot(),
                MAX_LIVE_WITHOUT_FOOD + 1000,
                MAX_LIVE_WITHOUT_FOOD);
    }
}
