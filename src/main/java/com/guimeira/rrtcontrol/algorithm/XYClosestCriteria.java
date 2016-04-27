package com.guimeira.rrtcontrol.algorithm;

public class XYClosestCriteria implements ClosestCriteria<Vector> {
    private CarModel model;

    public XYClosestCriteria(CarModel model) {
        this.model = model;
    }

    public double evaluate(Vector t1, Vector t2) {
        double x1 = model.getPosition(t1).getX();
        double y1 = model.getPosition(t1).getY();
        double x2 = model.getPosition(t2).getX();
        double y2 = model.getPosition(t2).getY();

        double xDiff = x1-x2;
        double yDiff = y1-y2;

        return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    }
}
