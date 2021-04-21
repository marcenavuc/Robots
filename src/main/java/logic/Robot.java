package logic;

import utils.Tuple;


import static utils.MyMath.*;

public class Robot /*implements Runnable*/ {
    private volatile Tuple<Double, Double> robotPosition;
    private volatile double robotDirection = 0;
    private GameObserver gameObserver;

    private volatile Tuple<Integer, Integer> targetPosition;
    private double satiety = 0;
    private volatile int hunger = 0;

    public final double MAX_VELOCITY = 0.1;
    public final double MAX_ANGULAR_VELOCITY = 0.001;



    public Robot(double robotPositionX, double robotPositionY) {
        setRobotPosition(robotPositionX, robotPositionY);
    }

    public void addObserver(GameObserver gameObserver){
        this.gameObserver = gameObserver;
    }

    public void setRobotPosition(double x, double y) {
        this.robotPosition = new Tuple<>(x, y);
    }

    private boolean canEat() {
        return targetPosition != null && findDistance(targetPosition.getKey(), targetPosition.getValue(),
                robotPosition.getKey(), robotPosition.getValue()) < 0.5;
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

        double angleToTarget = angleTo(robotPosition.getKey(), robotPosition.getValue(),
                targetPosition.getKey(), targetPosition.getValue());
        double angularVelocity = 0;
        if (angleToTarget > robotDirection)
            angularVelocity = MAX_ANGULAR_VELOCITY;

        if (angleToTarget < robotDirection)
            angularVelocity = -MAX_ANGULAR_VELOCITY;

        moveRobot(this, MAX_VELOCITY, angularVelocity, 10.0);
    }

    public Tuple<Integer, Integer> getRobotPosition() {
        return new Tuple<>(round(this.robotPosition.getKey()),
                round(this.robotPosition.getValue()));
    }

    public Tuple<Double, Double> getRobotPositionD() {
        return this.robotPosition;
    }

    public void setRobotDirection(double value) {
        this.robotDirection = value;
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
//        update();
//    }
}
