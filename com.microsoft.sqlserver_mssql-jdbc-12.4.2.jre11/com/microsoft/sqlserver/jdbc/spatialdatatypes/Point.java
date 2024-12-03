/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.spatialdatatypes;

public class Point {
    private final double x;
    private final double y;
    private final double z;
    private final double m;

    public Point(double x, double y, double z, double m) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.m = m;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getM() {
        return this.m;
    }
}

