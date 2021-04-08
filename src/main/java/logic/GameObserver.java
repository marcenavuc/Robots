package logic;

import utils.Math;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class GameObserver {
    private long counters;
    private ConcurrentHashMap<Long, Robot> robots;
    private CopyOnWriteArrayList<Food> foods;

    public GameObserver() {
        robots = new ConcurrentHashMap<>();
        foods = new CopyOnWriteArrayList<>();
    }

//    public GameObserver(ArrayList<Robot> robots, ArrayList<Food> foods) {
//        this.robots = robots;
//        this.foods = foods;
//    }

    public void attachFoodToRobot(Robot robot, Food food) {
        robot.setFood(food);
    }

    public Food findClosedFoodToRobot(Robot robot) {
        if (foods == null || foods.size() == 0)
            return null;
        double distance = Double.MAX_VALUE;
        Food nearestFood = foods.get(0);
        for (Food food : foods) {

            double currentDistance = Math.findDistance(
                    robot.getRobotPositionX(),
                    robot.getRobotPositionY(),
                    food.getPositionX(),
                    food.getPositionY());

            if (currentDistance < distance) {
                distance = currentDistance;
                nearestFood = food;
            }
        }
        return nearestFood;
    }

    private void deleteFoodFromMap(Food food) {
        foods.remove(food);
        for (long idxRobot: robots.keySet())
            if (robots.get(idxRobot).getFood() == food)
                robots.get(idxRobot).haveFood = false;
    }

    public void update() {
        if (!(robots != null && robots.keySet().size() != 0))
            return;
        //robots.removeIf(robot -> robot.getHunger() < 0);
        for (long idxRobot: robots.keySet()) {
            Robot robot = robots.get(idxRobot);
            if (!robot.haveFood & robot.getFood() != null) {
                Food robotFood = robot.getFood();
                deleteFoodFromMap(robotFood);
                robot.delFood();
            }
            if (!robot.haveFood & foods.size() > 0) {
                Food nearestFood = findClosedFoodToRobot(robot);
                attachFoodToRobot(robot, nearestFood);
            }
            robot.update();
        }
    }

    private void updateDirectionsToFood() {
        for (long idxRobot: robots.keySet()) {
            Robot robot = robots.get(idxRobot);
            Food nearestFood = findClosedFoodToRobot(robot);
            attachFoodToRobot(robot, nearestFood);
        }
    }

    public void setFoods(CopyOnWriteArrayList<Food> foods) {
        this.foods = foods;
    }

//    public void setRobots(ArrayList<Robot> robots) {
//        this.robots = robots;
//    }

    public List<Food> getFoods() {
        return foods;
    }

    public ConcurrentHashMap<Long, Robot> getMapRobots(){
        return robots;
    }

    public Collection<Robot> getRobots() {
        Collection<Robot> temp = robots.values();
        return temp;
    }

    public void addFood(Food food) {
        foods.add(food);
        updateDirectionsToFood();
    }

    public void addRobot(Robot robot) {
        robot.addObserver(this);
        robots.put(counters++, robot);
        updateDirectionsToFood();
    }

    public void updateSize(int width, int height) {
        for (long idxRobot: robots.keySet())
            robots.get(idxRobot).setSize(width, height);
    }
}
