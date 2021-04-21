package logic;

import utils.MyMath;
import utils.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class GameObserver {
    private long counters;
    private final ConcurrentHashMap<Long, Robot> robots;
    private final ConcurrentHashMap<Tuple<Integer, Integer>, Food> foods;
    private final long MAX_LIVE_WITHOUT_FOOD = 1000 * 5;

    public GameObserver() {
        robots = new ConcurrentHashMap<>();
        foods = new ConcurrentHashMap<>();
        initTimer();
    }

//    public GameObserver(ArrayList<Robot> robots, ArrayList<Food> foods) {
//        this.robots = robots;
//        this.foods = foods;
//    }

    public void attachFoodToRobot(Robot robot, Food food) {
        robot.setFood(food);
    }

    public Food findClosedFoodToRobot(Robot robot) {
        if (foods == null)
            return null;

        double distance = Double.MAX_VALUE;
        Food nearestFood = null;
        for (Food food : foods.values()) {

            double currentDistance = MyMath.findDistance(
                    robot.getRobotPosition().getKey(),
                    robot.getRobotPosition().getValue(),
                    food.getPositionX(),
                    food.getPositionY());

            if (currentDistance < distance) {
                distance = currentDistance;
                nearestFood = food;
            }
        }
        return nearestFood;
    }

    protected void deleteFoodFromMap(Tuple<Integer, Integer> target) {
        foods.keySet().remove(target);
        for (long idxRobot: robots.keySet()) {
            Tuple<Integer, Integer> targetPos = robots.get(idxRobot).getTarget();
            if (targetPos.getKey() - target.getKey() <= 0.5
                && targetPos.getValue() - target.getValue() <= 0.5)
                robots.get(idxRobot).delFood();
        }
    }

    public void update() {
        if (!(robots != null && robots.keySet().size() != 0))
            return;
        //robots.removeIf(robot -> robot.getHunger() < 0);
        for (long idxRobot: robots.keySet()) {
            if (robots.get(idxRobot) != null)
                robots.get(idxRobot).update();
        }
    }

    private void updateDirectionsToFood() {
        for (long idxRobot: robots.keySet()) {
            Robot robot = robots.get(idxRobot);
            Food nearestFood = findClosedFoodToRobot(robot);
            attachFoodToRobot(robot, nearestFood);
        }
    }

//    public void setFoods(CopyOnWriteArrayList<Food> foods) {
//        this.foods = foods;
//    }

//    public void setRobots(ArrayList<Robot> robots) {
//        this.robots = robots;
//    }

    public Collection<Food> getFoods() {
        return foods.values();
    }

    public ConcurrentHashMap<Long, Robot> getMapRobots(){
        return robots;
    }

    public Collection<Robot> getRobots() {
        return robots.values();
    }

    public void addFood(Food food) {
        foods.put(food.getPosition(), food);
        updateDirectionsToFood();
    }

    public void addRobot(Robot robot) {
        robot.addObserver(this);
        robots.put(counters++, robot);
        updateDirectionsToFood();
    }

    public void updateSize(int width, int height) {
        MyMath.setSize(width, height);
    }

    private class CheckAliveRobot extends TimerTask {
        public void run() {
            for (long id : robots.keySet()) {
                Robot robot = robots.get(id);
                if (robot.getHunger() <= 0)
                    getMapRobots().remove(id);
                else
                    robot.vichHunger();
            }
        }
    }

    private void initTimer(){
        Timer timer = new Timer("robots_timer");
        timer.scheduleAtFixedRate (new CheckAliveRobot(), MAX_LIVE_WITHOUT_FOOD + 1000, MAX_LIVE_WITHOUT_FOOD);
    }
}
