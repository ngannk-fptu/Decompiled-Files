/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.xy.Vector;
import org.jfree.data.xy.XYCoordinate;

public class VectorDataItem
extends ComparableObjectItem {
    public VectorDataItem(double x, double y, double deltaX, double deltaY) {
        super(new XYCoordinate(x, y), new Vector(deltaX, deltaY));
    }

    public double getXValue() {
        XYCoordinate xy = (XYCoordinate)this.getComparable();
        return xy.getX();
    }

    public double getYValue() {
        XYCoordinate xy = (XYCoordinate)this.getComparable();
        return xy.getY();
    }

    public Vector getVector() {
        return (Vector)this.getObject();
    }

    public double getVectorX() {
        Vector vi = (Vector)this.getObject();
        if (vi != null) {
            return vi.getX();
        }
        return Double.NaN;
    }

    public double getVectorY() {
        Vector vi = (Vector)this.getObject();
        if (vi != null) {
            return vi.getY();
        }
        return Double.NaN;
    }
}

