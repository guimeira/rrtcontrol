package com.guimeira.rrtcontrol.algorithm;

import java.awt.*;
import java.io.Serializable;

public class Rectangle implements Serializable {
    private final Point p1, p2, p3, p4;
    private Line[] lines;
    private Point[] points;

    public Rectangle(Point center, double width, double height, double theta) {
        Point originP1 = new Point(-width/2, -height/2);
        Point originP2 = new Point(width/2, -height/2);
        Point originP3 = new Point(width/2, height/2);
        Point originP4 = new Point(-width/2, height/2);

        Point rotatedP1 = originP1.rotate(theta);
        Point rotatedP2 = originP2.rotate(theta);
        Point rotatedP3 = originP3.rotate(theta);
        Point rotatedP4 = originP4.rotate(theta);

        p1 = rotatedP1.translate(center);
        p2 = rotatedP2.translate(center);
        p3 = rotatedP3.translate(center);
        p4 = rotatedP4.translate(center);

        points = new Point[] {p1, p2, p3, p4};
        lines = new Line[] {
                new Line(p1, p2),
                new Line(p2, p3),
                new Line(p3, p4),
                new Line(p4, p1)
        };
    }

    public Rectangle(Point p1, Point p2, Point p3, Point p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;

        points = new Point[] {p1, p2, p3, p4};
        lines = new Line[] {
                new Line(p1, p2),
                new Line(p2, p3),
                new Line(p3, p4),
                new Line(p4, p1)
        };
    }

    public Point[] getPoints() {
        Point[] pts = new Point[]{p1, p2, p3, p4};

        return pts;
    }

    public boolean intersects(Line l) {
        for(int i = 0; i < lines.length; i++) {
            if(lines[i].intersects(l)) {
                return true;
            }
        }

        if(isIn(l.getP1()) || isIn(l.getP2())) {
            return true;
        } else {
            return false;
        }
    }

    //See: http://www.gamedev.net/page/resources/_/technical/game-programming/2d-rotated-rectangle-collision-r2604
    public boolean intersects(Rectangle rect) {
        Point[] axis = new Point[] {
                p2.subtract(p1),
                p3.subtract(p2),
                rect.p2.subtract(rect.p1),
                rect.p3.subtract(rect.p2)
        };

        Point[] points = new Point[] {
                p1, p2, p3, p4, rect.p1, rect.p2, rect.p3, rect.p4
        };

        for(int i = 0; i < axis.length; i++) {
            double minR1 = Double.POSITIVE_INFINITY, maxR1 = Double.NEGATIVE_INFINITY;
            double minR2 = Double.POSITIVE_INFINITY, maxR2 = Double.NEGATIVE_INFINITY;

            for(int j = 0; j < points.length; j++) {
                Point projected = points[j].project(axis[i]);
                double position = projected.dot(axis[i]);

                if(j < 4) {
                    //First rectangle:
                    if(position < minR1) {
                        minR1 = position;
                    }

                    if(position > maxR1) {
                        maxR1 = position;
                    }
                } else {
                    //Second rectangle:
                    if(position < minR2) {
                        minR2 = position;
                    }

                    if(position > maxR2) {
                        maxR2 = position;
                    }
                }
            }

            if(minR2 > maxR1 || maxR2 < minR1) {
                return false;
            }
        }

        return true;
    }

    //See: http://stackoverflow.com/questions/11716268/point-in-polygon-algorithm
    public boolean isIn(Point p) {
        boolean c = false;

        for(int i = 0, j = points.length-1; i < points.length; j = i++) {
            if (((points[i].getY() >= p.getY()) != (points[j].getY() >= p.getY())) &&
                    (p.getX() <= (points[j].getX() - points[i].getX()) * (p.getY() - points[i].getY()) / (points[j].getY() - points[i].getY()) + points[i].getX())) {
                c = !c;
            }
        }

        return c;
    }

    public Rectangle translate(Point t) {
        return new Rectangle(p1.translate(t), p2.translate(t), p3.translate(t), p4.translate(t));
    }

    public Point center() {
        return new Point((p1.getX()+p3.getX())/2,(p1.getY()+p3.getY())/2);
    }

    public Rectangle rotate(double theta) {
        Point center = center();
        Rectangle atOrigin = translate(center.multiply(-1));
        Rectangle rotated = new Rectangle(atOrigin.p1.rotate(theta), atOrigin.p2.rotate(theta),
                atOrigin.p3.rotate(theta), atOrigin.p4.rotate(theta));
        return rotated.translate(center);
    }

    public void draw(Graphics2D g) {
        int[] xPts = new int[points.length];
        int[] yPts = new int[points.length];

        for(int i = 0; i < points.length; i++) {
            xPts[i] = (int) points[i].getX();
            yPts[i] = (int) points[i].getY();
        }

        g.fillPolygon(xPts, yPts, 4);
    }
}