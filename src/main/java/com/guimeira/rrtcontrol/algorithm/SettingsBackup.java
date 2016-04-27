package com.guimeira.rrtcontrol.algorithm;

import org.apache.commons.lang3.Range;

import java.io.Serializable;
import java.util.List;

public class SettingsBackup implements Serializable {
    private double[][] modelParameters;
    private double[] carParameters;
    private double[] plannerParameters;
    private int activeModel;
    private List<Rectangle> obstacles;
    private double startRotation, goalRotation;
    private Point startTranslation, goalTranslation;

    public double[][] getModelParameters() {
        return modelParameters;
    }

    public void setModelParameters(double[][] modelParameters) {
        this.modelParameters = modelParameters;
    }

    public double[] getCarParameters() {
        return carParameters;
    }

    public void setCarParameters(double[] carParameters) {
        this.carParameters = carParameters;
    }

    public double[] getPlannerParameters() {
        return plannerParameters;
    }

    public void setPlannerParameters(double[] plannerParameters) {
        this.plannerParameters = plannerParameters;
    }

    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Rectangle> obstacles) {
        this.obstacles = obstacles;
    }

    public double getStartRotation() {
        return startRotation;
    }

    public void setStartRotation(double startRotation) {
        this.startRotation = startRotation;
    }

    public double getGoalRotation() {
        return goalRotation;
    }

    public void setGoalRotation(double goalRotation) {
        this.goalRotation = goalRotation;
    }

    public Point getStartTranslation() {
        return startTranslation;
    }

    public void setStartTranslation(Point startTranslation) {
        this.startTranslation = startTranslation;
    }

    public Point getGoalTranslation() {
        return goalTranslation;
    }

    public void setGoalTranslation(Point goalTranslation) {
        this.goalTranslation = goalTranslation;
    }

    public int getActiveModel() {
        return activeModel;
    }

    public void setActiveModel(int activeModel) {
        this.activeModel = activeModel;
    }
}
