package com.guimeira.rrtcontrol.algorithm;

import java.util.List;
import java.util.Random;

public class KinematicModel implements CarModel {
    private double speed;
    private double length;
    private double width;

    private static final int STATE_X = 0;
    private static final int STATE_Y = 1;
    private static final int STATE_THETA = 2;

    @Override
    public String getModelName() {
        return "Kinematic Model";
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {};
    }

    @Override
    public void configure(CarParameters carParameters, double... parameters) {
        this.speed = carParameters.getSpeed();
        this.width = carParameters.getWidth();
        this.length = carParameters.getLength();
    }

    @Override
    public Vector derivatives(Vector state, double input) {
        double theta = state.getValue(STATE_THETA);
        double xDot = speed*Math.cos(theta);
        double yDot = speed*Math.sin(theta);
        double thetaDot = speed/length*Math.tan(input);
        return new Vector(xDot,yDot,thetaDot);
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

        return new Vector(sX, sY, sTheta);
    }

    @Override
    public Vector positionState(Point position, double theta) {
        return new Vector(position.getX(), position.getY(), theta);
    }

    @Override
    public String toString() {
        return getModelName();
    }
}
