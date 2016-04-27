package com.guimeira.rrtcontrol.algorithm;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class World {
    private int width;
    private int height;
    private List<Rectangle> rectangles;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        rectangles = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void add(Rectangle rect) {
        rectangles.add(rect);
    }

    public boolean intersects(Rectangle rectangle) {
        for(Rectangle r : rectangles) {
            if(rectangle.intersects(r)) {
                return true;
            }
        }

        return false;
    }

    public boolean intersects(Line line) {
        for(Rectangle r : rectangles) {
            if(r.intersects(line)) {
                return true;
            }
        }

        return false;
    }

    public void draw(Graphics2D g) {
        for(Rectangle r : rectangles) {
            r.draw(g);
        }
    }
}
