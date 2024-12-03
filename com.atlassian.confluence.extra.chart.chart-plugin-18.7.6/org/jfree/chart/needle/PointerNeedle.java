/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.needle.MeterNeedle;

public class PointerNeedle
extends MeterNeedle
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -4744677345334729606L;

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        GeneralPath shape1 = new GeneralPath();
        GeneralPath shape2 = new GeneralPath();
        float minX = (float)plotArea.getMinX();
        float minY = (float)plotArea.getMinY();
        float maxX = (float)plotArea.getMaxX();
        float maxY = (float)plotArea.getMaxY();
        float midX = (float)((double)minX + plotArea.getWidth() / 2.0);
        float midY = (float)((double)minY + plotArea.getHeight() / 2.0);
        shape1.moveTo(minX, midY);
        shape1.lineTo(midX, minY);
        shape1.lineTo(maxX, midY);
        shape1.closePath();
        shape2.moveTo(minX, midY);
        shape2.lineTo(midX, maxY);
        shape2.lineTo(maxX, midY);
        shape2.closePath();
        if (rotate != null && angle != 0.0) {
            this.getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            shape1.transform(this.getTransform());
            shape2.transform(this.getTransform());
        }
        if (this.getFillPaint() != null) {
            g2.setPaint(this.getFillPaint());
            g2.fill(shape1);
        }
        if (this.getHighlightPaint() != null) {
            g2.setPaint(this.getHighlightPaint());
            g2.fill(shape2);
        }
        if (this.getOutlinePaint() != null) {
            g2.setStroke(this.getOutlineStroke());
            g2.setPaint(this.getOutlinePaint());
            g2.draw(shape1);
            g2.draw(shape2);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PointerNeedle)) {
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

