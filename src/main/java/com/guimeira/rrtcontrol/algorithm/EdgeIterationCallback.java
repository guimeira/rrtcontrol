package com.guimeira.rrtcontrol.algorithm;

public interface EdgeIterationCallback<T,U> {
    void process(T from, T to, U content);
}
