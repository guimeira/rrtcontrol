package com.guimeira.rrtcontrol.algorithm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Tree<NodeContent,EdgeContent> {
    private Map<NodeContent,Pair<NodeContent,EdgeContent>> tree;

    public Tree() {
        tree = new HashMap<>();
    }

    public void insert(NodeContent node, NodeContent parent, EdgeContent content) {
        tree.put(node, new ImmutablePair<>(parent, content));
    }

    public NodeContent findClosest(NodeContent node, ClosestCriteria<NodeContent> crit) {
        NodeContent bestNode = null;
        double bestDistance = Double.POSITIVE_INFINITY;

        for(NodeContent n : tree.keySet()) {
            double distance = crit.evaluate(node, n);

            if(distance < bestDistance) {
                bestNode = n;
                bestDistance = distance;
            }
        }

        return bestNode;
    }

    public void iterateOverEdges(EdgeIterationCallback<NodeContent,EdgeContent> callback) {
        for(Map.Entry<NodeContent, Pair<NodeContent,EdgeContent>> entry : tree.entrySet()) {
            NodeContent parent = entry.getValue().getLeft();

            if(parent != null) {
                callback.process(entry.getKey(), parent, entry.getValue().getRight());
            }
        }
    }

    public void backtrack(EdgeIterationCallback<NodeContent,EdgeContent> callback, NodeContent startingNode) {
        NodeContent currentNode = startingNode;
        Pair<NodeContent,EdgeContent> parent;

        while(currentNode != null) {
            parent = tree.get(currentNode);
            callback.process(currentNode, parent.getLeft(), parent.getRight());
            currentNode = parent.getLeft();
        }
    }
}
