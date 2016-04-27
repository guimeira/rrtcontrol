package com.guimeira.rrtcontrol.gui;

import com.guimeira.rrtcontrol.algorithm.*;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PlannerPane extends JPanel implements PlannerListener {
    private MainWindow mainWindow;
    private JProgressBar plannerProgress;
    private JButton btnStart;
    private Planner planner;
    private boolean plannerExecuted;

    public PlannerPane(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        btnStart = new JButton("Start!");
        btnStart.setDisabledIcon(new ImageIcon(getClass().getResource("/images/running.gif")));
        btnStart.addActionListener(this::startPlanner);
        add(btnStart, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,10),0,0));

        plannerProgress = new JProgressBar();
        plannerProgress.setEnabled(false);
        add(plannerProgress, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,10,5,10),0,0));
    }

    private void startPlanner(ActionEvent e) {
        if(!plannerExecuted) {
            btnStart.setEnabled(false);
            mainWindow.setEnabled(false);

            PlannerParameters params = mainWindow.getPlannerParameters();
            planner = new Planner();
            planner.configure(params);
            planner.setPlannerListener(this);

            plannerProgress.setEnabled(true);
            plannerProgress.setMaximum(params.getIterations());
            new Thread(this::plannerThread).start();
        } else {
            btnStart.setText("Start!");
            mainWindow.setEnabled(true);
            plannerExecuted = false;
        }
    }

    private void plannerThread() {
        planner.plan();
    }

    @Override
    public void nodeAdded(CarModel model, int iteration, Vector from, Vector to) {
        SwingUtilities.invokeLater(() -> {
            plannerProgress.setValue(iteration);
            mainWindow.drawTreeLine((int)model.getPosition(from).getX(),
                    (int)model.getPosition(from).getY(),
                    (int)model.getPosition(to).getX(),
                    (int)model.getPosition(to).getY());
        });
    }

    @Override
    public void pathFound(CarModel model, List<Double> inputs, List<Vector> states) {
        SwingUtilities.invokeLater(() -> {
            mainWindow.drawFinalPath(model, states);
            plannerFinished();
        });
    }

    @Override
    public void pathNotFound() {
        SwingUtilities.invokeLater(this::plannerFinished);
    }

    private void plannerFinished() {
        btnStart.setEnabled(true);
        btnStart.setText("Clear");
        plannerProgress.setValue(0);
        plannerProgress.setEnabled(false);
        plannerExecuted = true;
    }
}
