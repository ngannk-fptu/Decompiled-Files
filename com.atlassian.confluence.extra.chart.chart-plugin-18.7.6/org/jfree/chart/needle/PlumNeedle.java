/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.needle.MeterNeedle;

public class PlumNeedle
extends MeterNeedle
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -3082660488660600718L;

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        Arc2D.Double shape = new Arc2D.Double(2);
        double radius = plotArea.getHeight();
        double halfX = plotArea.getWidth() / 2.0;
        double diameter = 2.0 * radius;
        shape.setFrame(plotArea.getMinX() + halfX - radius, plotArea.getMinY() - radius, diameter, diameter);
        radius = Math.toDegrees(Math.asin(halfX / radius));
        ((Arc2D)shape).setAngleStart(270.0 - radius);
        ((Arc2D)shape).setAngleExtent(2.0 * radius);
        Area s = new Area(shape);
        if (rotate != null && angle != 0.0) {
            this.getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            s.transform(this.getTransform());
        }
        this.defaultDisplay(g2, s);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PlumNeedle)) {
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

