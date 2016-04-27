package com.guimeira.rrtcontrol.gui;

import com.guimeira.rrtcontrol.algorithm.*;

import java.util.List;
import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private WorldPane worldPane;
    private ModelChooserPane modelChooserPane;
    private PlannerPane plannerPane;

    public MainWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        CarParameters initialParams = new CarParameters(20,30,50);
        worldPane = new WorldPane(this, initialParams);
        add(worldPane, BorderLayout.CENTER);

        modelChooserPane = new ModelChooserPane(this, initialParams, new KinematicModel(), new DynamicModel());
        JScrollPane modelChooserScroll = new JScrollPane(modelChooserPane);
        modelChooserScroll.setPreferredSize(new Dimension(300,(int)worldPane.getPreferredSize().getHeight()));
        add(modelChooserScroll, BorderLayout.EAST);

        plannerPane = new PlannerPane(this);
        add(plannerPane, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setTitle("RRT Control");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    PlannerParameters getPlannerParameters() {
        PlannerParameters params = new PlannerParameters();
        modelChooserPane.fillPlannerParameters(params);
        worldPane.fillPlannerParameters(params);
        params.setCriteria(new XYClosestCriteria(params.getModel()));

        return params;
    }

    void drawTreeLine(int xFrom, int yFrom, int xTo, int yTo) {
        worldPane.drawTreeLine(xFrom, yFrom, xTo, yTo);
    }

    void drawFinalPath(CarModel model, List<Vector> states) {
        worldPane.drawFinalPath(model, states);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled) {
            worldPane.enableDrawing();
        } else {
            worldPane.disableDrawing();
        }
        modelChooserPane.setEnabled(enabled);
    }

    void carParametersChanged(CarParameters newParams) {
        worldPane.setCarParameters(newParams);
    }

    void fillBackup(SettingsBackup backup) {
        worldPane.fillBackup(backup);
    }

    void restoreBackup(SettingsBackup backup) {
        worldPane.restoreBackup(backup);
    }
}
