package com.guimeira.rrtcontrol.algorithm;

import java.util.Random;

public interface CarModel {
    String[] getParameterNames();
    String getModelName();
    void configure(CarParameters carParameters, double... parameters);
    Vector derivatives(Vector state, double input);
    Point getPosition(Vector state);
    double getTheta(Vector state);
    double getWidth();
    double getLength();
    Vector randomState(Random randomGen, int width, int height);
    Vector positionState(Point position, double theta);
}
