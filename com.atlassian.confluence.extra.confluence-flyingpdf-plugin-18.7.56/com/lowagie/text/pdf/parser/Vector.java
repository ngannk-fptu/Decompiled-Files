/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.parser.Matrix;

public class Vector {
    private static final int I1 = 0;
    private static final int I2 = 1;
    private static final int I3 = 2;
    private final float[] values = new float[]{0.0f, 0.0f, 0.0f};

    public Vector(float x, float y, float z) {
        this.values[0] = x;
        this.values[1] = y;
        this.values[2] = z;
    }

    public float get(int index) {
        return this.values[index];
    }

    public Vector cross(Matrix by) {
        float x = this.values[0] * by.get(0) + this.values[1] * by.get(3) + this.values[2] * by.get(6);
        float y = this.values[0] * by.get(1) + this.values[1] * by.get(4) + this.values[2] * by.get(7);
        float z = this.values[0] * by.get(2) + this.values[1] * by.get(5) + this.values[2] * by.get(8);
        return new Vector(x, y, z);
    }

    public Vector subtract(Vector v) {
        float x = this.values[0] - v.values[0];
        float y = this.values[1] - v.values[1];
        float z = this.values[2] - v.values[2];
        return new Vector(x, y, z);
    }

    public Vector add(Vector v) {
        float x = this.values[0] + v.values[0];
        float y = this.values[1] + v.values[1];
        float z = this.values[2] + v.values[2];
        return new Vector(x, y, z);
    }

    public Vector cross(Vector with) {
        float x = this.values[1] * with.values[2] - this.values[2] * with.values[1];
        float y = this.values[2] * with.values[0] - this.values[0] * with.values[2];
        float z = this.values[0] * with.values[1] - this.values[1] * with.values[0];
        return new Vector(x, y, z);
    }

    public float dot(Vector with) {
        return this.values[0] * with.values[0] + this.values[1] * with.values[1] + this.values[2] * with.values[2];
    }

    public float length() {
        return (float)Math.sqrt(this.lengthSquared());
    }

    public float lengthSquared() {
        return this.values[0] * this.values[0] + this.values[1] * this.values[1] + this.values[2] * this.values[2];
    }

    public String toString() {
        return this.values[0] + "," + this.values[1] + "," + this.values[2];
    }
}

