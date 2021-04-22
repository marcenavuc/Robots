package logic;

import utils.Position;

import java.util.Collection;
import java.util.concurrent.*;

public class GameObserver {
    private long counters;
    private final ConcurrentHashMap<Long, Robot> robots;
    private final ConcurrentHashMap<Position, Food> foods;


    public GameObserver(int width, int height) {
        robots = new ConcurrentHashMap<>();
        foods = new ConcurrentHashMap<>();
        updateSize(width, height);
        //initTimer();
//        ThreadPoolExecutor executor =
//                (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
//        for (Robot robot : robots.values())
//            executor.submit(robot);
    }

    public void attachFoodToRobot(Robot robot, Food food) {
        robot.setFood(food);
    }

    protected void deleteFoodFromMap(Position target) {
        foods.keySet().remove(target);
        for (long idxRobot: robots.keySet()) {
            Position targetPos = robots.get(idxRobot).getTarget();
            if (targetPos.getX() - target.getX() <= 0.5
                && targetPos.getY() - target.getY() <= 0.5)
                robots.get(idxRobot).removeFood();
        }
    }

    public void update() {
        if (robots.keySet().size() == 0)
            return;
        //robots.removeIf(robot -> robot.getHunger() < 0);
        for (long idxRobot: robots.keySet()) {
            Robot robot = robots.get(idxRobot);
            if (robot.isAlive) {
//                if (!robot.checkStart())
//                    robot.start();
                robot.update();
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
        Core.setSize(width, height);
    }

    public Food findClosedFoodToRobot(Robot robot) {
        double distance = Double.MAX_VALUE;
        Food nearestFood = null;
        for (Food food : foods.values()) {

            double currentDistance = Core.findDistance(
                    robot.getRobotPosition().getX(),
                    robot.getRobotPosition().getY(),
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
