/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.needle.MeterNeedle;

public class PinNeedle
extends MeterNeedle
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -3787089953079863373L;

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        GeneralPath pointer = new GeneralPath();
        int minY = (int)plotArea.getMinY();
        int maxY = (int)plotArea.getMaxY();
        int midX = (int)(plotArea.getMinX() + plotArea.getWidth() / 2.0);
        int lenX = (int)(plotArea.getWidth() / 10.0);
        if (lenX < 2) {
            lenX = 2;
        }
        pointer.moveTo(midX - lenX, maxY - lenX);
        pointer.lineTo(midX + lenX, maxY - lenX);
        pointer.lineTo(midX, minY + lenX);
        pointer.closePath();
        lenX = 4 * lenX;
        Ellipse2D.Double circle = new Ellipse2D.Double(midX - lenX / 2, plotArea.getMaxY() - (double)lenX, lenX, lenX);
        Area shape = new Area(circle);
        shape.add(new Area(pointer));
        if (rotate != null && angle != 0.0) {
            this.getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            shape.transform(this.getTransform());
        }
        this.defaultDisplay(g2, shape);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PinNeedle)) {
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

