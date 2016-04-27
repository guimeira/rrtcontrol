package com.guimeira.rrtcontrol.algorithm;

import java.io.Serializable;

public class Line implements Serializable {
    private final Point p1, p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    //See: http://ideone.com/PnPJgb
    public boolean intersects(Line l) {
        Point a = p1;
        Point b = p2;
        Point c = l.p1;
        Point d = l.p2;

        Point cmp = c.subtract(a);
        Point r = b.subtract(a);
        Point s = d.subtract(c);

        double cmpxr = cmp.getX() * r.getY() - cmp.getY() * r.getX();
        double cmpxs = cmp.getX() * s.getY() - cmp.getY() * s.getX();
        double rxs = r.getX() * s.getY() - r.getY() * s.getX();

        if (cmpxr == 0f) {
            return ((c.getX() - a.getX() < 0) != (c.getX() - b.getX() < 0)) || ((c.getY() - a.getY() < 0) != (c.getY() - b.getY() < 0));
        }

        if (rxs == 0f)
            return false;

        double rxsr = 1f / rxs;
        double t = cmpxs * rxsr;
        double u = cmpxr * rxsr;

        return (t >= 0f) && (t <= 1f) && (u >= 0f) && (u <= 1f);
    }
}
