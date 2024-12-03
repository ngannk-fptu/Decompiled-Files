/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing.model;

import java.io.Serializable;

public class Point2D
implements Serializable {
    private static final long serialVersionUID = -5410937389829502498L;
    protected double x;
    protected double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getNumDimensions() {
        return 2;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(this.x);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.y);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Point2D other = (Point2D)obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y);
    }

    public static Point2D of(double x, double y) {
        return new Point2D(x, y);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}

