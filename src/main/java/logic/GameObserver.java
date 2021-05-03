package logic;

import gui.windows.ObserverFrame;
import utils.Core;
import utils.Tuple;

import java.util.Collection;
import java.util.concurrent.*;

public class GameObserver {
    private long counters;
    private final ConcurrentHashMap<Long, Robot> robots;
    private final ConcurrentHashMap<Tuple<Integer, Integer>, Food> foods;
    private final ObserverFrame observerFrame;
    private final ThreadPoolExecutor executor
            = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);


    public GameObserver(ObserverFrame observerFrame) {
        this.observerFrame = observerFrame;
        robots = new ConcurrentHashMap<>();
        foods = new ConcurrentHashMap<>();
//        initTimer();
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
//        for (Robot robot : robots.values())
//            executor.submit(robot);
    }

    public void attachFoodToRobot(Robot robot, Food food) {
        robot.setFood(food);
    }

    protected synchronized void deleteFoodFromMap(Tuple<Integer, Integer> target) {
        foods.keySet().remove(target);
        for (long idxRobot: robots.keySet()) {
            Tuple<Integer, Integer> targetPos = robots.get(idxRobot).getTarget();
            if (targetPos.getKey() - target.getKey() <= 1.5
                && targetPos.getValue() - target.getValue() <= 1.5)
                robots.get(idxRobot).delFood();
        }
        updateDirectionsToFood();
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
//                robots.get(idxRobot).update();
                executor.submit(robot);
            }
            else {
//                robot.interrupt();
                observerFrame.delRobot(robot.getId());
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

    public void addFood(int x, int y) {
        Food food = new Food(x, y, 1);
        foods.put(food.getPosition(), food);
        updateDirectionsToFood();
    }

    public void addRobot(int x, int y) {
        Robot robot = new Robot(x, y, counters++);
        observerFrame.addRobot(robot);
        robot.addObserver(this);
        robots.put(robot.getId(), robot);
        updateDirectionsToFood();
    }

    public void updateSize(int width, int height) {
        Core.setSize(width, height);
    }

    public Food findClosedFoodToRobot(Robot robot) {

        double distance = Double.MAX_VALUE;
        Food nearestFood = null;
        for (Food food : foods.values()) {

//            double currentDistance = Core.findDistance(
//                    robot.getRobotPosition().getKey(),
//                    robot.getRobotPosition().getValue(),
//                    food.getPositionX(),
//                    food.getPositionY());
            double currentDistance = Core.findDistance(robot, food);

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
