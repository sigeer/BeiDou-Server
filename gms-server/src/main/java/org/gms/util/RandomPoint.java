package org.gms.util;

import java.awt.Point;

public class RandomPoint {
    int minX;
    int maxX;
    int y;

    public RandomPoint(int minX, int maxX, int y) {
        this.minX = minX;
        this.maxX = maxX;
        this.y = y;
    }

    public Point getPoint() {
        return new Point(Randomizer.rand(minX, maxX), y);
    }
}
