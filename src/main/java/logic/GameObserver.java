package logic;

import utils.Math;

import java.util.ArrayList;
import java.util.List;

public class GameObserver {
    private ArrayList<Robot> robots;
    private ArrayList<Food> foods;

    public GameObserver() {
        robots = new ArrayList<Robot>();
        foods = new ArrayList<Food>();
    }

    public GameObserver(ArrayList<Robot> robots, ArrayList<Food> foods) {
        this.robots = robots;
        this.foods = foods;
    }

    public void attachFoodToRobot(Robot robot, Food food) {
        robot.setFood(food);
    }

    public Food findClosedFoodToRobot(Robot robot) {
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

    public void update() {
        if (robots == null | robots.size() == 0)
            return;
        robots.removeIf(robot -> robot.getHunger() < 0);
        for (Robot robot : robots) {
            if (!robot.haveFood & robot.getFood() != null) {
                Food robotFood = robot.getFood();
                foods.remove(robotFood);
            }
            if (!robot.haveFood & foods.size() > 0) {
                Food nearestFood = findClosedFoodToRobot(robot);
                attachFoodToRobot(robot, nearestFood);
            }
            robot.update();
        }
    }

    public void setFoods(ArrayList<Food> foods) {
        this.foods = foods;
    }

    public void setRobots(ArrayList<Robot> robots) {
        this.robots = robots;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public void addFood(Food food) {
        foods.add(food);
    }

    public void addRobot(Robot robot) {
        robots.add(robot);
    }

    public void updateSize(int width, int height) {
        for (Robot robot: robots) {
            robot.setSize(width, height);
        }
    }
}
