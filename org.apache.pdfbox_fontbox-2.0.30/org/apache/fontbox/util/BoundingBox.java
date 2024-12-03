/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.util;

import java.util.List;

public class BoundingBox {
    private float lowerLeftX;
    private float lowerLeftY;
    private float upperRightX;
    private float upperRightY;

    public BoundingBox() {
    }

    public BoundingBox(float minX, float minY, float maxX, float maxY) {
        this.lowerLeftX = minX;
        this.lowerLeftY = minY;
        this.upperRightX = maxX;
        this.upperRightY = maxY;
    }

    public BoundingBox(List<Number> numbers) {
        this.lowerLeftX = numbers.get(0).floatValue();
        this.lowerLeftY = numbers.get(1).floatValue();
        this.upperRightX = numbers.get(2).floatValue();
        this.upperRightY = numbers.get(3).floatValue();
    }

    public float getLowerLeftX() {
        return this.lowerLeftX;
    }

    public void setLowerLeftX(float lowerLeftXValue) {
        this.lowerLeftX = lowerLeftXValue;
    }

    public float getLowerLeftY() {
        return this.lowerLeftY;
    }

    public void setLowerLeftY(float lowerLeftYValue) {
        this.lowerLeftY = lowerLeftYValue;
    }

    public float getUpperRightX() {
        return this.upperRightX;
    }

    public void setUpperRightX(float upperRightXValue) {
        this.upperRightX = upperRightXValue;
    }

    public float getUpperRightY() {
        return this.upperRightY;
    }

    public void setUpperRightY(float upperRightYValue) {
        this.upperRightY = upperRightYValue;
    }

    public float getWidth() {
        return this.getUpperRightX() - this.getLowerLeftX();
    }

    public float getHeight() {
        return this.getUpperRightY() - this.getLowerLeftY();
    }

    public boolean contains(float x, float y) {
        return x >= this.lowerLeftX && x <= this.upperRightX && y >= this.lowerLeftY && y <= this.upperRightY;
    }

    public String toString() {
        return "[" + this.getLowerLeftX() + "," + this.getLowerLeftY() + "," + this.getUpperRightX() + "," + this.getUpperRightY() + "]";
    }
}

