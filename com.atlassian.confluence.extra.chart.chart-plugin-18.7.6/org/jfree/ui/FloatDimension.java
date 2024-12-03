/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.geom.Dimension2D;
import java.io.Serializable;

public class FloatDimension
extends Dimension2D
implements Serializable {
    private static final long serialVersionUID = 5367882923248086744L;
    private float width;
    private float height;

    public FloatDimension() {
        this.width = 0.0f;
        this.height = 0.0f;
    }

    public FloatDimension(FloatDimension fd) {
        this.width = fd.width;
        this.height = fd.height;
    }

    public FloatDimension(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setWidth(double width) {
        this.width = (float)width;
    }

    public void setHeight(double height) {
        this.height = (float)height;
    }

    public void setSize(double width, double height) {
        this.setHeight((float)height);
        this.setWidth((float)width);
    }

    public Object clone() {
        return super.clone();
    }

    public String toString() {
        return this.getClass().getName() + ":={width=" + this.getWidth() + ", height=" + this.getHeight() + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloatDimension)) {
            return false;
        }
        FloatDimension floatDimension = (FloatDimension)o;
        if (this.height != floatDimension.height) {
            return false;
        }
        return this.width == floatDimension.width;
    }

    public int hashCode() {
        int result = Float.floatToIntBits(this.width);
        result = 29 * result + Float.floatToIntBits(this.height);
        return result;
    }
}

