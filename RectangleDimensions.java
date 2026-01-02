import java.io.*;
import java.util.*;
class RectangleDimensions {
    double length;
    double width;
    RectangleDimensions(double length, double width) {
        this.length = length;
        this.width = width;
    }
}
class RectangleArea {
    double calculateArea(RectangleDimensions dimensions) {
        return dimensions.length * dimensions.width;
    }
}
public class MainClass {
    public static void main(String[] args) {
        RectangleDimensions dimensions = new RectangleDimensions(5.0, 3.0);
        RectangleArea areaCalculator = new RectangleArea();
        double area = areaCalculator.calculateArea(dimensions);
        System.out.println("The area of the rectangle is: " + area);
    }
}