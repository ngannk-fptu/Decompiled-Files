/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

public class ContentLimit {
    public static final int UNDEFINED = -1;
    private int _top = -1;
    private int _bottom = -1;

    public int getTop() {
        return this._top;
    }

    public void setTop(int top) {
        this._top = top;
    }

    public void updateTop(int top) {
        if (this._top == -1 || top < this._top) {
            this._top = top;
        }
    }

    public int getBottom() {
        return this._bottom;
    }

    public void setBottom(int bottom) {
        this._bottom = bottom;
    }

    public void updateBottom(int bottom) {
        if (this._bottom == -1 || bottom > this._bottom) {
            this._bottom = bottom;
        }
    }

    public String toString() {
        return "[top=" + this._top + ", bottom=" + this._bottom + "]";
    }
}

