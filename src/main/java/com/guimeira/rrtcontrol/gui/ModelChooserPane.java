package com.guimeira.rrtcontrol.gui;

import com.guimeira.rrtcontrol.algorithm.*;
import org.apache.commons.lang3.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.NumberFormat;

public class ModelChooserPane extends JPanel {
    private MainWindow mainWindow;
    private CarModel[] models;
    private JComboBox<CarModel> cmbModels;
    private CardLayout modelParamsLayout;
    private JPanel modelParamsPanel;
    private ParameterPanel[] modelParameterPanels;
    private ParameterPanel carParameterPanel;
    private ParameterPanel plannerParameterPanel;
    private JButton btnSave, btnLoad;
    private String[] carParameterNames = new String[] {"Speed", "Width", "Length"};
    private String[] plannerParameterNames = new String[] {"Reach goal threshold", "Time increment (s)", "Max. iterations", "Min. steering angle (deg)", "Max. steering angle (deg)", "Steering increments (deg)", "Try goal probability", "Random seed"};

    public ModelChooserPane(MainWindow mainWindow, CarParameters carParameters, CarModel... models) {
        this.mainWindow = mainWindow;
        this.models = models;

        cmbModels = new JComboBox<>(models);
        cmbModels.addActionListener(this::modelChanged);

        modelParamsPanel = new JPanel();
        modelParamsLayout = new CardLayout();
        modelParamsPanel.setLayout(modelParamsLayout);
        modelParameterPanels = new ParameterPanel[models.length];

        for(int i = 0; i < models.length; i++) {
            CarModel m = models[i];
            modelParameterPanels[i] = new ParameterPanel("ModelParameters", m.getParameterNames());
            modelParamsPanel.add(modelParameterPanels[i], m.getModelName());
        }

        int gridRow = 0;
        setLayout(new GridBagLayout());
        add(cmbModels, new GridBagConstraints(0,gridRow++,2,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,5,10),0,0));
        add(modelParamsPanel, new GridBagConstraints(0,gridRow++,2,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,10),0,0));
        carParameterPanel = new ParameterPanel("Car parameters", carParameterNames);
        carParameterPanel.setParameterChangeListener(this::carParameterChanged);
        add(carParameterPanel, new GridBagConstraints(0,gridRow++,2,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,10),0,0));
        plannerParameterPanel = new ParameterPanel("Planner parameters", plannerParameterNames);
        add(plannerParameterPanel, new GridBagConstraints(0,gridRow++,2,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,10),0,0));

        btnSave = new JButton("Save");
        btnSave.addActionListener(this::generateBackup);
        add(btnSave, new GridBagConstraints(0,gridRow,1,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
        btnLoad= new JButton("Load");
        btnLoad.addActionListener(this::restoreBackup);
        add(btnLoad, new GridBagConstraints(1,gridRow++,1,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,10),0,0));

        add(Box.createGlue(), new GridBagConstraints(0,gridRow++,2,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

        setCarParameters(carParameters);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cmbModels.setEnabled(enabled);
        carParameterPanel.setEnabled(enabled);
        plannerParameterPanel.setEnabled(enabled);

        for(ParameterPanel p : modelParameterPanels) {
            p.setEnabled(enabled);
        }
    }

    public CarParameters getCarParameters() {
        double[] params = carParameterPanel.getValues();
        return new CarParameters(params[0], params[1], params[2]);
    }

    private CarModel getModel() {
        CarModel model = (CarModel) cmbModels.getSelectedItem();
        double[] modelParams = modelParameterPanels[cmbModels.getSelectedIndex()].getValues();
        model.configure(getCarParameters(), modelParams);
        return model;
    }

    public void fillPlannerParameters(PlannerParameters params) {
        double[] values = plannerParameterPanel.getValues();
        params.setReachGoalThreshold(values[0]);
        params.setDeltaT(values[1]);
        params.setIterations((int)values[2]);
        params.setSteeringRange(Range.between(Math.toRadians(values[3]),Math.toRadians(values[4])));
        params.setSteeringIncrement(Math.toRadians(values[5]));
        params.setTryGoalProbability(values[6]);
        params.setModel(getModel());
        params.setRandomSeed((long)values[7]);
    }

    private void generateBackup(ActionEvent e) {
        SettingsBackup backup = new SettingsBackup();
        double[][] modelParameters = new double[models.length][];

        for(int i = 0; i < models.length; i++) {
            modelParameters[i] = modelParameterPanels[i].getValues();
        }
        backup.setModelParameters(modelParameters);
        backup.setCarParameters(carParameterPanel.getValues());
        backup.setPlannerParameters(plannerParameterPanel.getValues());
        backup.setActiveModel(cmbModels.getSelectedIndex());
        mainWindow.fillBackup(backup);

        JFileChooser fc = new JFileChooser();
        int returnValue = fc.showSaveDialog(mainWindow);

        if(returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
                os.writeObject(backup);
                os.close();
                JOptionPane.showMessageDialog(mainWindow, "Settings saved successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainWindow, "IO error when writing the settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreBackup(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        int returnValue = fc.showOpenDialog(mainWindow);
        SettingsBackup backup = null;

        if(returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();

                if(!f.exists()) {
                    JOptionPane.showMessageDialog(mainWindow, "File does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));
                backup = (SettingsBackup) is.readObject();
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainWindow, "IO error when reading the settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            if(backup != null) {
                double[][] modelParams = backup.getModelParameters();

                for(int i = 0; i < modelParams.length; i++) {
                    modelParameterPanels[i].setValues(modelParams[i]);
                }
                carParameterPanel.setValues(backup.getCarParameters());
                plannerParameterPanel.setValues(backup.getPlannerParameters());
                cmbModels.setSelectedIndex(backup.getActiveModel());
                mainWindow.restoreBackup(backup);
                mainWindow.carParametersChanged(getCarParameters());
                setEnabled(true);
                JOptionPane.showMessageDialog(mainWindow, "Settings loaded successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void carParameterChanged(double[] newParams) {
        CarParameters newCarParams = new CarParameters(newParams[0],newParams[1],newParams[2]);
        mainWindow.carParametersChanged(newCarParams);
    }

    private void modelChanged(ActionEvent e) {
        CarModel selectedModel = (CarModel) cmbModels.getSelectedItem();
        modelParamsLayout.show(modelParamsPanel, selectedModel.getModelName());
    }

    private void setCarParameters(CarParameters carParameters) {
        carParameterPanel.setValues(new double[]{carParameters.getSpeed(), carParameters.getWidth(), carParameters.getLength()});
    }

    private class ParameterPanel extends JPanel {
        private JFormattedTextField[] txtValues;
        private ParameterChangeListener listener;

        public ParameterPanel(String title, String[] parameterNames) {
            setLayout(new GridBagLayout());
            int gridRow = 0;

            if(parameterNames.length > 0) {
                txtValues = new JFormattedTextField[parameterNames.length];
                int i = 0;

                for (String s : parameterNames) {
                    JLabel lblName = new JLabel(s);
                    JFormattedTextField txtValue = new JFormattedTextField(NumberFormat.getNumberInstance());
                    txtValue.setValue(0);
                    txtValue.setColumns(12);
                    txtValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtValue.getPreferredSize().height));
                    txtValue.addPropertyChangeListener("value",this::parameterChanged);
                    add(lblName, new GridBagConstraints(0, gridRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
                    add(txtValue, new GridBagConstraints(0, gridRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                    txtValues[i++] = txtValue;
                }
            } else {
                JLabel lblNoParams = new JLabel("No parameters");
                add(lblNoParams, new GridBagConstraints(0, gridRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
            }

            add(Box.createGlue(), new GridBagConstraints(0,gridRow++,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
            setBorder(BorderFactory.createTitledBorder(title));
        }

        public void setParameterChangeListener(ParameterChangeListener listener) {
            this.listener = listener;
        }

        public double[] getValues() {
            if(txtValues == null) {
                return null;
            }

            double[] values = new double[txtValues.length];

            for(int i = 0; i < values.length; i++) {
                try {
                    txtValues[i].commitEdit();
                } catch(Exception e) {
                    e.printStackTrace();
                }

                values[i] = ((Number) txtValues[i].getValue()).doubleValue();
            }

            return values;
        }

        public void setValues(double[] values) {
            if(values != null) {
                for (int i = 0; i < txtValues.length; i++) {
                    txtValues[i].setValue(values[i]);
                }
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);

            if(txtValues != null) {
                for (JFormattedTextField t : txtValues) {
                    t.setEnabled(enabled);
                }
            }

            btnSave.setEnabled(enabled);
            btnLoad.setEnabled(enabled);
        }

        private void parameterChanged(PropertyChangeEvent e) {
            if(listener != null) {
                listener.parameterChanged(getValues());
            }
        }
    }

    private interface ParameterChangeListener {
        void parameterChanged(double[] newValues);
    }
}
