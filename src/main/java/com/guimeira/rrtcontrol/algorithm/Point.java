package com.guimeira.rrtcontrol.algorithm;

import java.io.Serializable;

public class Point implements Serializable {
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public double dot(Point p) {
        return x*p.x + y*p.y;
    }

    public Point multiply(Point p) {
        return new Point(x*p.x, y*p.y);
    }

    public Point multiply(double constant) {
        return new Point(x*constant, y*constant);
    }

    public Point divide(double constant) {
        return new Point(x/constant, y/constant);
    }

    public Point project(Point axis) {
        return axis.multiply(this.dot(axis)/axis.dot(axis));
    }

    public Point rotate(double theta) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        return new Point(x*cos - y*sin, x*sin+y*cos);
    }

    public Point translate(Point p) {
        return new Point(x + p.x, y + p.y);
    }
}
