package com.guimeira.rrtcontrol.algorithm;

import java.util.List;

public interface PlannerListener {
    void nodeAdded(CarModel model, int iteration, Vector from, Vector to);
    void pathFound(CarModel model, List<Double> inputs, List<Vector> states);
    void pathNotFound();
}
