package com.guimeira.rrtcontrol.algorithm;

import java.util.Random;

public class DynamicModel implements CarModel {
    private double speed;
    private double length;
    private double width;
    private double cf, cr;
    private double mass;
    private double inertia;
    private double lf, lr;

    private static final int STATE_VY = 0;
    private static final int STATE_R = 1;
    private static final int STATE_X = 2;
    private static final int STATE_Y = 3;
    private static final int STATE_THETA = 4;

    @Override
    public String[] getParameterNames() {
        return new String[] {
                "Front cornering stiffness",
                "Rear cornering stiffness",
                "Mass",
                "Inertia"
        };
    }

    @Override
    public String getModelName() {
        return "Dynamic Model";
    }

    @Override
    public void configure(CarParameters carParameters, double... parameters) {
        this.speed = carParameters.getSpeed();
        this.width = carParameters.getWidth();
        this.length = carParameters.getLength();
        this.cf = parameters[0];
        this.cr = parameters[1];
        this.mass = parameters[2];
        this.inertia = parameters[3];
        this.lf = this.lr = length/2;
    }

    @Override
    public Vector derivatives(Vector state, double input) {
        double vy = state.getValue(STATE_VY);
        double r = state.getValue(STATE_R);
        double x = state.getValue(STATE_X);
        double y = state.getValue(STATE_Y);
        double theta = state.getValue(STATE_THETA);

        double cosInput = Math.cos(input);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        double a = -(cf*cosInput+cr)/(mass*speed);
        double b = (-lf*cf*cosInput+lr*cr)/(mass*speed)-speed;
        double c = (-lf*cf*cosInput+lr*cr)/(inertia*speed);
        double d = -(lf*lf*cf*cosInput+lr*lr*cr)/(inertia*speed);
        double e = cf*cosInput/mass;
        double f = lf*cf*cosInput/inertia;

        double vyDot = a*vy + c*r + e*input;
        double rDot = b*vy + d*r + f*input;
        double xDot = speed*cosTheta - vy*sinTheta;
        double yDot = speed*sinTheta + vy*cosTheta;
        double thetaDot = r;

        return new Vector(vyDot, rDot, xDot, yDot, thetaDot);
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public Point getPosition(Vector state) {
        return new Point(state.getValue(STATE_X), state.getValue(STATE_Y));
    }

    @Override
    public double getTheta(Vector state) {
        return state.getValue(STATE_THETA);
    }

    @Override
    public Vector randomState(Random randomGen, int width, int height) {
        double sX = randomGen.nextDouble()*width;
        double sY = randomGen.nextDouble()*height;
        double sTheta = randomGen.nextDouble()*2*Math.PI;

        return new Vector(0, 0, sX, sY, sTheta);
    }

    @Override
    public Vector positionState(Point position, double theta) {
        return new Vector(0, 0, position.getX(), position.getY(), theta);
    }

    @Override
    public String toString() {
        return getModelName();
    }
}
