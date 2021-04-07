package logic;

public class Food {
    private volatile int positionX;
    private volatile int positionY;

    private  volatile double satiety;

    public Food(int positionX, int positionY, double satiety) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.satiety = satiety;
    }

    public void setPosition(int x, int y) {
        positionX = x;
        positionY = y;
    }

    public void setSatiety(double satiety) {
        this.satiety = satiety;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public double getSatiety() {
        return satiety;
    }
}
