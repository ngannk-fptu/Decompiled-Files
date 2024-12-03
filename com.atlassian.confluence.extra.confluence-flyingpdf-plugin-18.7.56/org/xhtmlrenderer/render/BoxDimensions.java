/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

public class BoxDimensions {
    private int _leftMBP;
    private int _rightMBP;
    private int _contentWidth;
    private int _height;

    public int getContentWidth() {
        return this._contentWidth;
    }

    public void setContentWidth(int contentWidth) {
        this._contentWidth = contentWidth;
    }

    public int getHeight() {
        return this._height;
    }

    public void setHeight(int height) {
        this._height = height;
    }

    public int getLeftMBP() {
        return this._leftMBP;
    }

    public void setLeftMBP(int leftMBP) {
        this._leftMBP = leftMBP;
    }

    public int getRightMBP() {
        return this._rightMBP;
    }

    public void setRightMBP(int rightMBP) {
        this._rightMBP = rightMBP;
    }
}

