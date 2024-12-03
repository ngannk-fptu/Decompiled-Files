/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface ShapePainter {
    public void paint(Graphics2D var1);

    public Shape getPaintedArea();

    public Rectangle2D getPaintedBounds2D();

    public boolean inPaintedArea(Point2D var1);

    public Shape getSensitiveArea();

    public Rectangle2D getSensitiveBounds2D();

    public boolean inSensitiveArea(Point2D var1);

    public void setShape(Shape var1);

    public Shape getShape();
}

