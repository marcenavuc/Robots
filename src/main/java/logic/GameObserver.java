package logic;

import utils.MyMath;
import utils.Tuple;

import java.util.Collection;
import java.util.concurrent.*;

public class GameObserver {
    private long counters;
    private final ConcurrentHashMap<Long, Robot> robots;
    private final ConcurrentHashMap<Tuple<Integer, Integer>, Food> foods;


    public GameObserver() {
        robots = new ConcurrentHashMap<>();
        foods = new ConcurrentHashMap<>();
        //initTimer();
//        ThreadPoolExecutor executor =
//                (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
//        for (Robot robot : robots.values())
//            executor.submit(robot);
    }

    public void attachFoodToRobot(Robot robot, Food food) {
        robot.setFood(food);
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
            Robot robot = robots.get(idxRobot);
            if (robot.isAlive) {
//                if (!robot.checkStart())
//                    robot.start();
                                robots.get(idxRobot).update();
            }
            else {
//                robot.interrupt();
                robots.remove(idxRobot);
            }
        }
    }

    public Collection<Food> getFoods() {
        return foods.values();
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

    private void updateDirectionsToFood() {
        for (long idxRobot: robots.keySet()) {
            Robot robot = robots.get(idxRobot);
            Food nearestFood = findClosedFoodToRobot(robot);
            attachFoodToRobot(robot, nearestFood);
        }
    }
}
