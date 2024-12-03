/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Box2D
implements Serializable {
    private static final long serialVersionUID = -1855277817131669241L;
    protected double[] coordinates;
    protected double[] sides;

    public Box2D(double width, double height) {
        this(0.0, 0.0, width, height);
    }

    public Box2D(double x, double y, double width, double height) {
        this(new double[2], new double[2]);
        assert (width >= 0.0 && height >= 0.0);
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        this.sides[0] = width;
        this.sides[1] = height;
    }

    public Box2D(double[] coordinates, double[] sides) {
        assert (coordinates.length == 2);
        assert (sides.length == 2);
        this.coordinates = Objects.requireNonNull(coordinates);
        this.sides = Objects.requireNonNull(sides);
        if (coordinates.length != sides.length) {
            throw new IllegalArgumentException("Box dimensions do not match");
        }
    }

    public double getMinX() {
        return this.coordinates[0];
    }

    public double getMinY() {
        return this.coordinates[1];
    }

    public double getWidth() {
        return this.sides[0];
    }

    public double getHeight() {
        return this.sides[1];
    }

    public double getMaxX() {
        return this.coordinates[0] + this.sides[0];
    }

    public double getMaxY() {
        return this.coordinates[1] + this.sides[1];
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.coordinates);
        result = 31 * result + Arrays.hashCode(this.sides);
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
        Box2D other = (Box2D)obj;
        if (!Arrays.equals(this.coordinates, other.coordinates)) {
            return false;
        }
        return Arrays.equals(this.sides, other.sides);
    }

    public String toString() {
        return "Box2D [minX=" + this.coordinates[0] + ", minY=" + this.coordinates[1] + ", width=" + this.sides[0] + ", height=" + this.sides[1] + "]";
    }

    public static Box2D of(double width, double height) {
        return new Box2D(new double[]{0.0, 0.0}, new double[]{width, height});
    }

    public static Box2D of(double x, double y, double width, double height) {
        return new Box2D(new double[]{x, y}, new double[]{width, height});
    }
}

