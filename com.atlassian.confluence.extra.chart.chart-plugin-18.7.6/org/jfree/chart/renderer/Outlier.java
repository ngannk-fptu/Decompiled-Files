/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.awt.geom.Point2D;

public class Outlier
implements Comparable {
    private Point2D point;
    private double radius;

    public Outlier(double xCoord, double yCoord, double radius) {
        this.point = new Point2D.Double(xCoord - radius, yCoord - radius);
        this.radius = radius;
    }

    public Point2D getPoint() {
        return this.point;
    }

    public void setPoint(Point2D point) {
        this.point = point;
    }

    public double getX() {
        return this.getPoint().getX();
    }

    public double getY() {
        return this.getPoint().getY();
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int compareTo(Object o) {
        Point2D p2;
        Outlier outlier = (Outlier)o;
        Point2D p1 = this.getPoint();
        if (p1.equals(p2 = outlier.getPoint())) {
            return 0;
        }
        if (p1.getX() < p2.getX() || p1.getY() < p2.getY()) {
            return -1;
        }
        return 1;
    }

    public boolean overlaps(Outlier other) {
        return other.getX() >= this.getX() - this.radius * 1.1 && other.getX() <= this.getX() + this.radius * 1.1 && other.getY() >= this.getY() - this.radius * 1.1 && other.getY() <= this.getY() + this.radius * 1.1;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Outlier)) {
            return false;
        }
        Outlier that = (Outlier)obj;
        if (!this.point.equals(that.point)) {
            return false;
        }
        return this.radius == that.radius;
    }

    public String toString() {
        return "{" + this.getX() + "," + this.getY() + "}";
    }
}

