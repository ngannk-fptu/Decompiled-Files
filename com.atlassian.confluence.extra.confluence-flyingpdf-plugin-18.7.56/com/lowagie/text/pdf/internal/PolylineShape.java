/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.internal.PolylineShapeIterator;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PolylineShape
implements Shape {
    protected int[] x;
    protected int[] y;
    protected int np;

    public PolylineShape(int[] x, int[] y, int nPoints) {
        this.np = nPoints;
        this.x = new int[this.np];
        this.y = new int[this.np];
        System.arraycopy(x, 0, this.x, 0, this.np);
        System.arraycopy(y, 0, this.y, 0, this.np);
    }

    @Override
    public Rectangle2D getBounds2D() {
        int[] r = this.rect();
        return r == null ? null : new Rectangle2D.Double(r[0], r[1], r[2], r[3]);
    }

    @Override
    public Rectangle getBounds() {
        return this.getBounds2D().getBounds();
    }

    private int[] rect() {
        if (this.np == 0) {
            return null;
        }
        int xMin = this.x[0];
        int yMin = this.y[0];
        int xMax = this.x[0];
        int yMax = this.y[0];
        for (int i = 1; i < this.np; ++i) {
            if (this.x[i] < xMin) {
                xMin = this.x[i];
            } else if (this.x[i] > xMax) {
                xMax = this.x[i];
            }
            if (this.y[i] < yMin) {
                yMin = this.y[i];
                continue;
            }
            if (this.y[i] <= yMax) continue;
            yMax = this.y[i];
        }
        return new int[]{xMin, yMin, xMax - xMin, yMax - yMin};
    }

    @Override
    public boolean contains(double x, double y) {
        return false;
    }

    @Override
    public boolean contains(Point2D p) {
        return false;
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return false;
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return false;
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return this.intersects(new Rectangle2D.Double(x, y, w, h));
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        if (this.np == 0) {
            return false;
        }
        Line2D.Double line = new Line2D.Double(this.x[0], this.y[0], this.x[0], this.y[0]);
        for (int i = 1; i < this.np; ++i) {
            ((Line2D)line).setLine(this.x[i - 1], this.y[i - 1], this.x[i], this.y[i]);
            if (!line.intersects(r)) continue;
            return true;
        }
        return false;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new PolylineShapeIterator(this, at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new PolylineShapeIterator(this, at);
    }
}

