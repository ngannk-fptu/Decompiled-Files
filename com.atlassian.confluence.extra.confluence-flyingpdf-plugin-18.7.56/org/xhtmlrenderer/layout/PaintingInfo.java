/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

public class PaintingInfo {
    private Dimension _outerMarginCorner;
    private Rectangle _aggregateBounds;

    public Rectangle getAggregateBounds() {
        return this._aggregateBounds;
    }

    public void setAggregateBounds(Rectangle aggregateBounds) {
        this._aggregateBounds = aggregateBounds;
    }

    public Dimension getOuterMarginCorner() {
        return this._outerMarginCorner;
    }

    public void setOuterMarginCorner(Dimension outerMarginCorner) {
        this._outerMarginCorner = outerMarginCorner;
    }

    public PaintingInfo copyOf() {
        PaintingInfo result = new PaintingInfo();
        result.setOuterMarginCorner(new Dimension(this._outerMarginCorner));
        result.setAggregateBounds(new Rectangle(this._aggregateBounds));
        return result;
    }

    public void translate(int tx, int ty) {
        this._aggregateBounds.translate(tx, ty);
        this._outerMarginCorner.setSize(this._outerMarginCorner.getWidth() + (double)tx, this._outerMarginCorner.getHeight() + (double)ty);
    }
}

