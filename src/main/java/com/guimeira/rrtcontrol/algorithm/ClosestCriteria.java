package com.guimeira.rrtcontrol.algorithm;

public interface ClosestCriteria<T> {
    double evaluate(T t1, T t2);
}
