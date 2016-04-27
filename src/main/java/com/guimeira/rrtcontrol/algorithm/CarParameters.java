package com.guimeira.rrtcontrol.algorithm;

public class CarParameters {
    private double speed;
    private double width;
    private double length;

    public CarParameters(double speed, double width, double length) {
        this.speed = speed;
        this.width = width;
        this.length = length;
    }

    public double getSpeed() {
        return speed;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }
}
