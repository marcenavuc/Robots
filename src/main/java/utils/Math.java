package utils;

public class Math {

    public static double findDistance(double x1, double y1,
                                   double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return java.lang.Math.sqrt(diffX * diffX + diffY * diffY);
    }
}
