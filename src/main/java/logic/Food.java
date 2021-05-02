package logic;

import utils.Tuple;

public class Food {
    private final int positionX;
    private final int positionY;

    private final double satiety;

    public Food(int positionX, int positionY, double satiety) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.satiety = satiety;
    }

//    public void setPosition(int x, int y) {
//        positionX = x;
//        positionY = y;
//    }
//
//    public void setSatiety(double satiety) {
//        this.satiety = satiety;
//    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public double getSatiety() {
        return satiety;
    }

    public Tuple<Integer, Integer> getPosition() {
        return new Tuple<>(positionX, positionY);
    }
}
