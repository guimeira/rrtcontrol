package com.guimeira.rrtcontrol.algorithm;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Planner {
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
    private PlannerListener listener;
    private int iteration;
    private Random randomGen;

    public void configure(PlannerParameters params) {
        this.world = params.getWorld();
        this.criteria = params.getCriteria();
        this.model = params.getModel();
        this.initialState = params.getInitialState();
        this.finalState = params.getFinalState();
        this.reachGoalThreshold = params.getReachGoalThreshold();
        this.deltaT = params.getDeltaT();
        this.iterations = params.getIterations();
        this.steeringRange = params.getSteeringRange();
        this.steeringIncrement = params.getSteeringIncrement();
        this.tryGoalProbability = params.getTryGoalProbability();
        this.randomGen = params.getRandomSeed() == 0 ? new Random() : new Random(params.getRandomSeed());
    }

    public void plan() {
        Tree<Vector,Double> tree = new Tree<>();
        tree.insert(initialState, null, 0.0);
        Vector goalState = null;

        for(iteration = 0; iteration < iterations; iteration++) {
            Vector stateRand;

            if(randomGen.nextDouble() < tryGoalProbability) {
                stateRand = finalState;
            } else {
                stateRand = randomConfig();
            }

            Vector newState;
            double previousDistance = Double.POSITIVE_INFINITY;

            newState = extend(stateRand, tree);

            if(newState != null && isCloseEnough(newState, finalState)) {
                goalState = newState;

                if(listener != null) {
                    Pair<List<Double>,List<Vector>> path = getPath(tree, goalState);
                    listener.pathFound(model, path.getLeft(), path.getRight());
                }

                break;
            }
        }

        if(goalState == null && listener != null) {
            listener.pathNotFound();
        }

        //saveImage(tree, goalState);
    }

    private Pair<List<Double>,List<Vector>> getPath(Tree<Vector,Double> graph, Vector goalState) {
        List<Double> inputSequence = new ArrayList<>();
        List<Vector> stateSequence = new ArrayList<>();

        graph.backtrack((n1, n2, content) -> {
            if(n2 != null) {
                inputSequence.add(content);
                stateSequence.add(n1);
            }
        }, goalState);

        Collections.reverse(inputSequence);
        Collections.reverse(stateSequence);

        return new ImmutablePair<>(inputSequence, stateSequence);
    }

    public void setPlannerListener(PlannerListener listener) {
        this.listener = listener;
    }

    private boolean isCloseEnough(Vector v1, Vector v2) {
        return criteria.evaluate(v1,v2) < reachGoalThreshold;
    }

    /*private void saveImage(Tree<Vector,Double> g, Vector goalState) {
        BufferedImage image = new BufferedImage(world.getWidth(),world.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = image.createGraphics();

        world.draw(graphics);

        g.iterateOverEdges((n1,n2,content) -> {
            graphics.drawLine((int)model.getPosition(n1).getX(),(int)model.getPosition(n1).getY(),(int)model.getPosition(n2).getX(),(int)model.getPosition(n2).getY());
        });

        if(goalState != null) {
            List<Double> inputSequence = new ArrayList<>();

            g.backtrack((n1,n2,content) -> {
                if(n2 != null) {
                    inputSequence.add(content);
                }
            }, goalState);

            System.out.println(inputSequence);
            Vector currentState = initialState;

            graphics.setColor(Color.RED);
            for(int i = inputSequence.size()-1; i >= 0; i--) {
                Vector nextState = nextState(currentState,inputSequence.get(i));
                graphics.setColor(Color.GREEN);
                graphics.drawLine((int)model.getPosition(currentState).getX(), (int)model.getPosition(currentState).getY(), (int)model.getPosition(nextState).getX(), (int)model.getPosition(nextState).getY());

                graphics.setColor(Color.RED);
                Rectangle car = new Rectangle(model.getPosition(nextState),model.getLength(),model.getWidth(),model.getTheta(nextState));
                car.draw(graphics);
                currentState = nextState;
            }
        }

        try {
            ImageIO.write(image,"png",new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private Vector randomConfig() {
        Vector stateRand;
        Rectangle rect;

        do {
            stateRand = model.randomState(randomGen, world.getWidth(), world.getHeight());
            rect = new Rectangle(model.getPosition(stateRand), model.getLength(), model.getWidth(), model.getTheta(stateRand));
        } while(world.intersects(rect));

        return stateRand;
    }

    private Vector extend(Vector state, Tree<Vector,Double> tree) {
        Vector nearest = tree.findClosest(state,criteria);
        Pair<Vector,Double> input = selectInput(nearest, state);
        Vector newState = input.getLeft();
        Line path = new Line(model.getPosition(state), model.getPosition(newState));
        Rectangle car = new Rectangle(model.getPosition(newState),model.getLength(),model.getWidth(),model.getTheta(newState));

        if(!world.intersects(path) && !world.intersects(car)) {
            tree.insert(newState, nearest, input.getRight());

            if(listener != null) {
                listener.nodeAdded(model, iteration, nearest, newState);
            }
            return newState;
        }

        return null;
    }

    private Vector nextState(Vector state, double input) {
        //Fourth-order Runge-Kutta:
        Vector k1 = model.derivatives(state, input);
        Vector k2 = model.derivatives(state.add(k1.divide(2)),input);
        Vector k3 = model.derivatives(state.add(k2.divide(2)),input);
        Vector k4 = model.derivatives(state.add(k3),input);

        Vector sumK = k1.add(k2.multiply(2)).add(k3.multiply(3)).add(k4);
        return state.add(sumK.multiply(deltaT/6));
    }

    private Pair<Vector,Double> selectInput(Vector from, Vector to) {
        Vector bestState = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        double bestAngle = Double.POSITIVE_INFINITY;

        for(double s = steeringRange.getMinimum(); s < steeringRange.getMaximum(); s += steeringIncrement) {
            Vector state = nextState(from,s);
            double distance = criteria.evaluate(state,to);

            if(distance < bestDistance) {
                bestState = state;
                bestDistance = distance;
                bestAngle = s;
            }
        }

        return new ImmutablePair<Vector,Double>(bestState, bestAngle);
    }
}
