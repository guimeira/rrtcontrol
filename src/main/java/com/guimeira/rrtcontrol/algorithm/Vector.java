package com.guimeira.rrtcontrol.algorithm;

import java.util.Arrays;

public class Vector {
    private double[] values;

    public Vector(double... values) {
        this.values = values;
    }

    public double getValue(int n) {
        return values[n];
    }

    public double[] getValues() {
        return values;
    }

    public Vector add(Vector v) {
        if(values.length != v.getValues().length) {
            throw new IllegalArgumentException();
        }

        double[] newValues = new double[values.length];

        for(int i = 0; i < values.length; i++) {
            newValues[i] = values[i] + v.getValue(i);
        }

        return new Vector(newValues);
    }

    public Vector subtract(Vector v) {
        if(values.length != v.getValues().length) {
            throw new IllegalArgumentException();
        }

        double[] newValues = new double[values.length];

        for(int i = 0; i < values.length; i++) {
            newValues[i] = values[i] - v.getValue(i);
        }

        return new Vector(newValues);
    }

    public double dot(Vector v) {
        if(values.length != v.getValues().length) {
            throw new IllegalArgumentException();
        }

        double sum = 0;
        for(int i = 0; i < values.length; i++) {
            sum += values[i] * v.getValue(i);
        }

        return sum;
    }

    public Vector divide(double constant) {
        double[] newValues = new double[values.length];

        for(int i = 0; i < values.length; i++) {
            newValues[i] = values[i]/constant;
        }

        return new Vector(newValues);
    }

    public Vector multiply(double constant) {
        double[] newValues = new double[values.length];

        for(int i = 0; i < values.length; i++) {
            newValues[i] = values[i]*constant;
        }

        return new Vector(newValues);
    }

    public double distance(Vector vector) {
        Vector diff = subtract(vector);
        return Math.sqrt(diff.dot(diff));
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
