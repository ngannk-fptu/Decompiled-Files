/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.needle.MeterNeedle;

public class ArrowNeedle
extends MeterNeedle
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -5334056511213782357L;
    private boolean isArrowAtTop = true;

    public ArrowNeedle(boolean isArrowAtTop) {
        this.isArrowAtTop = isArrowAtTop;
    }

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        Line2D.Float shape = new Line2D.Float();
        Shape d = null;
        float x = (float)(plotArea.getMinX() + plotArea.getWidth() / 2.0);
        float minY = (float)plotArea.getMinY();
        float maxY = (float)plotArea.getMaxY();
        ((Line2D)shape).setLine(x, minY, x, maxY);
        GeneralPath shape1 = new GeneralPath();
        if (this.isArrowAtTop) {
            shape1.moveTo(x, minY);
            minY += (float)(4 * this.getSize());
        } else {
            shape1.moveTo(x, maxY);
            minY = maxY - (float)(4 * this.getSize());
        }
        shape1.lineTo(x + (float)this.getSize(), minY);
        shape1.lineTo(x - (float)this.getSize(), minY);
        shape1.closePath();
        if (rotate != null && angle != 0.0) {
            this.getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            d = this.getTransform().createTransformedShape(shape);
        } else {
            d = shape;
        }
        this.defaultDisplay(g2, d);
        d = rotate != null && angle != 0.0 ? this.getTransform().createTransformedShape(shape1) : shape1;
        this.defaultDisplay(g2, d);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrowNeedle)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        ArrowNeedle that = (ArrowNeedle)obj;
        return this.isArrowAtTop == that.isArrowAtTop;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = HashUtilities.hashCode(result, this.isArrowAtTop);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

