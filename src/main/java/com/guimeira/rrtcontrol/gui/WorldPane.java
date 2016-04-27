package com.guimeira.rrtcontrol.gui;

import com.guimeira.rrtcontrol.algorithm.*;
import com.guimeira.rrtcontrol.algorithm.Point;
import com.guimeira.rrtcontrol.algorithm.Rectangle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class WorldPane extends JPanel implements MouseInputListener {
    private MainWindow mainWindow;
    private Mode mode;
    private List<Rectangle> obstacles;
    private double startRotation, goalRotation;
    private Point startTranslation, goalTranslation;
    private CarParameters carParameters;
    private Rectangle activeRectangle;
    private Point startClickPosition;
    private Point currentMousePosition;
    private BufferedImage imgCarStart, imgCarGoal;
    private BufferedImage worldImage;
    private Graphics2D worldImageGraphics;
    private static final int SIZE = 500;

    public WorldPane(MainWindow mainWindow, CarParameters initialCarParameters) {
        this.mainWindow = mainWindow;
        mode = Mode.DRAWING;
        obstacles = new ArrayList<>();
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        setPreferredSize(new Dimension(SIZE,SIZE));
        setMaximumSize(getPreferredSize());

        try {
            imgCarStart = ImageIO.read(getClass().getResourceAsStream("/images/car_start.png"));
            imgCarGoal = ImageIO.read(getClass().getResourceAsStream("/images/car_goal.png"));
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainWindow, "Could not load image resources.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        startTranslation = new Point(50,50);
        startRotation = 0;

        goalTranslation = new Point(450,450);
        goalRotation = 0;

        carParameters = initialCarParameters;
    }

    public void fillPlannerParameters(PlannerParameters params) {
        World world = new World(SIZE,SIZE);

        for(Rectangle r : obstacles) {
            world.add(r);
        }

        params.setWorld(world);
        params.setInitialState(params.getModel().positionState(startTranslation, startRotation));
        params.setFinalState(params.getModel().positionState(goalTranslation, goalRotation));
    }

    public void fillBackup(SettingsBackup backup) {
        backup.setGoalRotation(goalRotation);
        backup.setGoalTranslation(goalTranslation);
        backup.setStartRotation(startRotation);
        backup.setStartTranslation(startTranslation);
        backup.setObstacles(obstacles);
    }

    public void restoreBackup(SettingsBackup backup) {
        goalRotation = backup.getGoalRotation();
        goalTranslation = backup.getGoalTranslation();
        startRotation = backup.getStartRotation();
        startTranslation = backup.getStartTranslation();
        obstacles = backup.getObstacles();
        enableDrawing();
    }

    public void setCarParameters(CarParameters params) {
        carParameters = params;
        repaint();
    }

    public void enableDrawing() {
        mode = Mode.DRAWING;
        repaint();
    }

    public void disableDrawing() {
        worldImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        worldImageGraphics = worldImage.createGraphics();
        paint(worldImageGraphics);
        mode = Mode.DISABLED;
    }

    public void drawTreeLine(int xFrom, int yFrom, int xTo, int yTo) {
        worldImageGraphics.setColor(Color.RED);
        worldImageGraphics.drawLine(xFrom, yFrom, xTo, yTo);
        repaint();
    }

    public void drawFinalPath(CarModel model, List<Vector> states) {
        worldImageGraphics.setColor(Color.BLUE);
        worldImageGraphics.setStroke(new BasicStroke(4));

        Vector previous = null;

        for(Vector s : states) {
            if(previous != null) {
                worldImageGraphics.drawLine((int)model.getPosition(previous).getX(),
                        (int)model.getPosition(previous).getY(),
                        (int)model.getPosition(s).getX(),
                        (int)model.getPosition(s).getY());
            }
            previous = s;
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(mode == Mode.DISABLED) {
            return;
        }

        Point p = new Point(e.getX(), e.getY());
        startClickPosition = p;

        activeRectangle = null;

        Rectangle startRect = new Rectangle(startTranslation,carParameters.getLength(),carParameters.getWidth(),startRotation);
        Rectangle goalRect = new Rectangle(goalTranslation,carParameters.getLength(),carParameters.getWidth(),goalRotation);

        if(startRect.isIn(p)) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                mode = Mode.MOVING_START;
                return;
            } else if(e.getButton() == MouseEvent.BUTTON3) {
                mode = Mode.ROTATING_START;
                return;
            }
        } else if(goalRect.isIn(p)) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                mode = Mode.MOVING_GOAL;
                return;
            } else if(e.getButton() == MouseEvent.BUTTON3) {
                mode = Mode.ROTATING_GOAL;
                return;
            }
        }

        for(Rectangle r : obstacles) {
            if(r.isIn(p)) {
                activeRectangle = r;
                obstacles.remove(r);

                if(e.getButton() == MouseEvent.BUTTON1) {
                    mode = Mode.MOVING;
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    mode = Mode.ROTATING;
                } else if(e.getButton() == MouseEvent.BUTTON2) {
                    mode = Mode.DRAWING;
                    return;
                }
                break;
            }
        }

        if(activeRectangle == null && e.getButton() == MouseEvent.BUTTON1) {
            mode = Mode.CREATING;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(mode == Mode.DISABLED) {
            return;
        }

        Point p = new Point(e.getX(), e.getY());
        Rectangle startRect = new Rectangle(startTranslation,carParameters.getLength(),carParameters.getWidth(),startRotation);
        Rectangle goalRect = new Rectangle(goalTranslation,carParameters.getLength(),carParameters.getWidth(),goalRotation);

        switch(mode) {
            case CREATING:
                double width = p.getX() - startClickPosition.getX();
                double height = p.getY() - startClickPosition.getY();

                Rectangle rect = new Rectangle(
                        startClickPosition,
                        startClickPosition.add(new Point(width,0)),
                        startClickPosition.add(new Point(width,height)),
                        startClickPosition.add(new Point(0,height)));

                if(!rect.intersects(startRect) && !rect.intersects(goalRect)) {
                    obstacles.add(rect);
                } else {
                    JOptionPane.showMessageDialog(mainWindow, "Obstacle intersects with start or goal.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                break;

            case MOVING:
                Point translation = currentMousePosition.subtract(startClickPosition);
                Rectangle newRect = activeRectangle.translate(translation);
                if(!newRect.intersects(startRect) && !newRect.intersects(goalRect)) {
                    obstacles.add(newRect);
                } else {
                    JOptionPane.showMessageDialog(mainWindow, "Obstacle intersects with start or goal.", "Error", JOptionPane.INFORMATION_MESSAGE);
                    obstacles.add(activeRectangle);
                }
                break;

            case ROTATING:
                Point center = activeRectangle.center();
                double initialTheta = Math.atan2(startClickPosition.getY()-center.getY(),startClickPosition.getX()-center.getX());
                double theta = Math.atan2(currentMousePosition.getY()-center.getY(),currentMousePosition.getX()-center.getX());
                Rectangle rotatedRect = activeRectangle.rotate(theta-initialTheta);
                if(!rotatedRect.intersects(startRect) && !rotatedRect.intersects(goalRect)) {
                    obstacles.add(rotatedRect);
                } else {
                    JOptionPane.showMessageDialog(mainWindow, "Obstacle intersects with start or goal.", "Error", JOptionPane.INFORMATION_MESSAGE);
                    obstacles.add(activeRectangle);
                }
                break;

            case MOVING_START:
                Point startPos = currentMousePosition.subtract(startClickPosition);

                Rectangle translatedStart = startRect.translate(startPos);
                boolean translatedStartIntersects = false;

                for(Rectangle r : obstacles) {
                    if(r.intersects(translatedStart)) {
                        translatedStartIntersects = true;
                        break;
                    }
                }

                if(!translatedStartIntersects) {
                    startTranslation = startTranslation.translate(startPos);
                } else {
                    JOptionPane.showMessageDialog(mainWindow, "Start position intersects with obstacle.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                break;

            case MOVING_GOAL:
                Point goalPos = currentMousePosition.subtract(startClickPosition);
                Rectangle translatedGoal = goalRect.translate(goalPos);
                boolean translatedGoalIntersects = false;

                for(Rectangle r : obstacles) {
                    if(r.intersects(translatedGoal)) {
                        translatedGoalIntersects = true;
                        break;
                    }
                }

                if(!translatedGoalIntersects) {
                    goalTranslation = goalTranslation.translate(goalPos);
                } else {
                    JOptionPane.showMessageDialog(mainWindow, "Goal position intersects with obstacle.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }

                break;

            case ROTATING_START:
                double startInitialTheta = Math.atan2(startClickPosition.getY()-startTranslation.getY(),startClickPosition.getX()-startTranslation.getX());
                double startTheta = Math.atan2(currentMousePosition.getY()-startTranslation.getY(),currentMousePosition.getX()-startTranslation.getX()) - startInitialTheta;
                Rectangle rotatedStart = startRect.rotate(startTheta);
                boolean rotatedStartIntersects = false;

                for(Rectangle r : obstacles) {
                    if(r.intersects(rotatedStart)) {
                        rotatedStartIntersects = true;
                        break;
                    }
                }

                if(!rotatedStartIntersects) {
                    startRotation += startTheta;
                } else {
                    JOptionPane.showMessageDialog(mainWindow, "Start position intersects with obstacle.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                break;

            case ROTATING_GOAL:
                double goalInitialTheta = Math.atan2(startClickPosition.getY()-goalTranslation.getY(),startClickPosition.getX()-goalTranslation.getX());
                double goalTheta = Math.atan2(currentMousePosition.getY()-goalTranslation.getY(),currentMousePosition.getX()-goalTranslation.getX()) - goalInitialTheta;
                Rectangle rotatedGoal = startRect.rotate(goalTheta);
                boolean rotatedGoalIntersects = false;

                for(Rectangle r : obstacles) {
                    if(r.intersects(rotatedGoal)) {
                        rotatedGoalIntersects = true;
                        break;
                    }
                }

                if(!rotatedGoalIntersects) {
                    goalRotation += goalTheta;
                } else {
                    JOptionPane.showMessageDialog(mainWindow, "Goal position intersects with obstacle.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
        }

        mode = Mode.DRAWING;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(mode == Mode.DISABLED) {
            return;
        }

        Point p = new Point(e.getX(), e.getY());
        currentMousePosition = p;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.BLACK);

        if(mode == Mode.DISABLED) {
            g2d.drawImage(worldImage, 0, 0, null);
        } else {
            for (Rectangle r : obstacles) {
                r.draw(g2d);
            }

            switch (mode) {
                case CREATING:
                    int x = (int) Math.min(startClickPosition.getX(), currentMousePosition.getX());
                    int y = (int) Math.min(startClickPosition.getY(), currentMousePosition.getY());
                    int width = (int) Math.abs(startClickPosition.getX() - currentMousePosition.getX());
                    int height = (int) Math.abs(startClickPosition.getY() - currentMousePosition.getY());

                    g.setColor(Color.RED);
                    g.fillRect(x, y, width, height);
                    break;

                case MOVING:
                    Point translation = currentMousePosition.subtract(startClickPosition);
                    Rectangle paintRect = activeRectangle.translate(translation);
                    g.setColor(Color.GREEN);
                    paintRect.draw(g2d);
                    break;

                case ROTATING:
                    Point center = activeRectangle.center();
                    double initialTheta = Math.atan2(startClickPosition.getY() - center.getY(), startClickPosition.getX() - center.getX());
                    double theta = Math.atan2(currentMousePosition.getY() - center.getY(), currentMousePosition.getX() - center.getX());
                    Rectangle rotatedRect = activeRectangle.rotate(theta - initialTheta);
                    g.setColor(Color.YELLOW);
                    rotatedRect.draw(g2d);
                    break;

                case MOVING_START:
                    Point startPos = currentMousePosition.subtract(startClickPosition).translate(startTranslation);
                    drawImage(g2d, imgCarStart, carParameters.getLength(), carParameters.getWidth(), startRotation, startPos.getX(), startPos.getY());
                    break;

                case MOVING_GOAL:
                    Point goalPos = currentMousePosition.subtract(startClickPosition).translate(goalTranslation);
                    drawImage(g2d, imgCarGoal, carParameters.getLength(), carParameters.getWidth(), goalRotation, goalPos.getX(), goalPos.getY());
                    break;

                case ROTATING_START:
                    double startInitialTheta = Math.atan2(startClickPosition.getY() - startTranslation.getY(), startClickPosition.getX() - startTranslation.getX());
                    double startTheta = Math.atan2(currentMousePosition.getY() - startTranslation.getY(), currentMousePosition.getX() - startTranslation.getX()) - startInitialTheta;
                    drawImage(g2d, imgCarStart, carParameters.getLength(), carParameters.getWidth(), startRotation + startTheta, startTranslation.getX(), startTranslation.getY());
                    break;

                case ROTATING_GOAL:
                    double goalInitialTheta = Math.atan2(startClickPosition.getY() - goalTranslation.getY(), startClickPosition.getX() - goalTranslation.getX());
                    double goalTheta = Math.atan2(currentMousePosition.getY() - goalTranslation.getY(), currentMousePosition.getX() - goalTranslation.getX()) - goalInitialTheta;
                    drawImage(g2d, imgCarGoal, carParameters.getLength(), carParameters.getWidth(), goalRotation + goalTheta, goalTranslation.getX(), goalTranslation.getY());
                    break;
            }
        }

        if(mode != Mode.MOVING_START && mode != Mode.ROTATING_START) {
            drawImage(g2d, imgCarStart, carParameters.getLength(), carParameters.getWidth(), startRotation, startTranslation.getX(), startTranslation.getY());
        }

        if(mode != Mode.MOVING_GOAL && mode != Mode.ROTATING_GOAL) {
            drawImage(g2d, imgCarGoal, carParameters.getLength(), carParameters.getWidth(), goalRotation, goalTranslation.getX(), goalTranslation.getY());
        }
    }

    private void drawImage(Graphics2D g2d, BufferedImage image, double width, double height, double theta, double x, double y) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(theta);
        transform.scale(width/image.getWidth(), height/image.getHeight());
        transform.translate(-image.getWidth()/2, -image.getHeight()/2);
        AffineTransformOp startTransformOp = new AffineTransformOp(transform,AffineTransformOp.TYPE_BILINEAR);
        g2d.drawImage(image,startTransformOp,(int)x,(int)y);
    }


    private enum Mode {
        DRAWING, MOVING, ROTATING, CREATING, MOVING_START, ROTATING_START, MOVING_GOAL, ROTATING_GOAL, DISABLED
    }
}
