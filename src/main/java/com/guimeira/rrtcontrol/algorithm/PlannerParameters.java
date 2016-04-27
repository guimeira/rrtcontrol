package com.guimeira.rrtcontrol.algorithm;

import org.apache.commons.lang3.Range;

public class PlannerParameters {
    private World world;
    private ClosestCriteria criteria;
    private CarModel model;
    private Vector initialState;
    private Vector finalState;
    private double reachGoalThreshold;
    private double deltaT;
    private int iterations;
    private Range<Double> steeringRange;
    private double steeringIncrement;
    private double tryGoalProbability;
    private long randomSeed;

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public ClosestCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(ClosestCriteria criteria) {
        this.criteria = criteria;
    }

    public CarModel getModel() {
        return model;
    }

    public void setModel(CarModel model) {
        this.model = model;
    }

    public Vector getInitialState() {
        return initialState;
    }

    public void setInitialState(Vector initialState) {
        this.initialState = initialState;
    }

    public Vector getFinalState() {
        return finalState;
    }

    public void setFinalState(Vector finalState) {
        this.finalState = finalState;
    }

    public double getReachGoalThreshold() {
        return reachGoalThreshold;
    }

    public void setReachGoalThreshold(double reachGoalThreshold) {
        this.reachGoalThreshold = reachGoalThreshold;
    }

    public double getDeltaT() {
        return deltaT;
    }

    public void setDeltaT(double deltaT) {
        this.deltaT = deltaT;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public Range<Double> getSteeringRange() {
        return steeringRange;
    }

    public void setSteeringRange(Range<Double> steeringRange) {
        this.steeringRange = steeringRange;
    }

    public double getSteeringIncrement() {
        return steeringIncrement;
    }

    public void setSteeringIncrement(double steeringIncrement) {
        this.steeringIncrement = steeringIncrement;
    }

    public double getTryGoalProbability() {
        return tryGoalProbability;
    }

    public void setTryGoalProbability(double tryGoalProbability) {
        this.tryGoalProbability = tryGoalProbability;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }
}
