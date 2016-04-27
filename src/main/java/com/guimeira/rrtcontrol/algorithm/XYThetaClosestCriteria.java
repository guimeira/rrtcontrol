package com.guimeira.rrtcontrol.algorithm;

public class XYThetaClosestCriteria implements ClosestCriteria<Vector> {
    private CarModel model;

    public XYThetaClosestCriteria(CarModel model) {
        this.model = model;
    }

    public double evaluate(Vector t1, Vector t2) {
        double x1 = model.getPosition(t1).getX();
        double y1 = model.getPosition(t1).getY();
        double theta1 = model.getTheta(t1);
        double x2 = model.getPosition(t2).getX();
        double y2 = model.getPosition(t2).getY();
        double theta2 = model.getTheta(t2);

        /*while(theta1 < 0) theta1 += Math.PI*2;
        while(theta1 > 2*Math.PI) theta1 -= Math.PI*2;
        while(theta2 < 0) theta2 += Math.PI*2;
        while(theta2 > 2*Math.PI) theta2 -= Math.PI*2;*/

        double xDiff = x1-x2;
        double yDiff = y1-y2;
        double thetaDiff = Math.abs(theta1-theta2);

        thetaDiff = Math.min(thetaDiff, 2*Math.PI-thetaDiff);
        return Math.sqrt(xDiff*xDiff + yDiff*yDiff + thetaDiff*thetaDiff);
    }
}
