package logic;

import utils.Tuple;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static utils.MyMath.*;

public class Robot implements Observable {
    private java.util.List<Observer> listObservers;
    private volatile Tuple<Double, Double> robotPosition;
    private volatile double robotDirection = 0;
    private GameObserver gameObserver;

    private volatile Tuple<Integer, Integer> targetPosition;
    private double satiety = 0;
    private volatile int hunger = 0;

    public final double MAX_VELOCITY = 0.1;
    public final double MAX_ANGULAR_VELOCITY = 0.001;
    private final long MAX_LIVE_WITHOUT_FOOD = 1000 * 20;

    public boolean isAlive;
//    private final Thread thread;
    private Timer timer;
    public static Lock lock = new ReentrantLock();
    public boolean isPainted = false;
    private final long id;


    public Robot(double robotPositionX, double robotPositionY, long id) {
        this.id = id;
        listObservers = new ArrayList<>();
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
        this.robotPosition = new Tuple<>(x, y);
        notifyObservers();
    }

    public void setRobotDirection(double value) {
        this.robotDirection = value;
    }

    private boolean canEat() {
        return targetPosition != null && findDistance(targetPosition.getKey(), targetPosition.getValue(), robotPosition.getKey(), robotPosition.getValue()) < 0.5;
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

        double angleToTarget = angleTo(robotPosition.getKey(), robotPosition.getValue(), targetPosition.getKey(), targetPosition.getValue());
        double angularVelocity = 0;
        if (angleToTarget > robotDirection)
            angularVelocity = MAX_ANGULAR_VELOCITY;

        if (angleToTarget < robotDirection)
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        moveRobot(this, MAX_VELOCITY, angularVelocity, 10.0);
    }

    public Tuple<Integer, Integer> getRobotPosition() {
        return new Tuple<>(round(this.robotPosition.getKey()), round(this.robotPosition.getValue()));
    }

    public Tuple<Double, Double> getRobotPositionD() {
        return this.robotPosition;
    }

    public double getRobotDirection() {
        return this.robotDirection;
    }

    public Tuple<Integer, Integer> getTarget() {
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

//    @Override
//    public void run() {
//            while (isAlive) {
//                isPainted = false;
//                update();
//                lock.lock();
//            }
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

    @Override
    public void removeObserver(Observer o)
    {
        listObservers.remove(o);
    }
    @Override
    public void registerObserver(Observer o) {
        listObservers.add(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : listObservers) {
            observer.update(robotPosition, robotDirection, targetPosition, id);
        }
    }

    public long getId() {
        return id;
    }
}
