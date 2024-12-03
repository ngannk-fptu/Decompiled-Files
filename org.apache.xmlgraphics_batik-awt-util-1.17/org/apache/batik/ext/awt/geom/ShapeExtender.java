/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.geom.ExtendedPathIterator;
import org.apache.batik.ext.awt.geom.ExtendedShape;

public class ShapeExtender
implements ExtendedShape {
    Shape shape;

    public ShapeExtender(Shape shape) {
        this.shape = shape;
    }

    @Override
    public boolean contains(double x, double y) {
        return this.shape.contains(x, y);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return this.shape.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Point2D p) {
        return this.shape.contains(p);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return this.shape.contains(r);
    }

    @Override
    public Rectangle getBounds() {
        return this.shape.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return this.shape.getBounds2D();
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return this.shape.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return this.shape.getPathIterator(at, flatness);
    }

    @Override
    public ExtendedPathIterator getExtendedPathIterator() {
        return new EPIWrap(this.shape.getPathIterator(null));
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return this.shape.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return this.shape.intersects(r);
    }

    public static class EPIWrap
    implements ExtendedPathIterator {
        PathIterator pi = null;

        public EPIWrap(PathIterator pi) {
            this.pi = pi;
        }

        @Override
        public int currentSegment() {
            float[] coords = new float[6];
            return this.pi.currentSegment(coords);
        }

        @Override
        public int currentSegment(double[] coords) {
            return this.pi.currentSegment(coords);
        }

        @Override
        public int currentSegment(float[] coords) {
            return this.pi.currentSegment(coords);
        }

        @Override
        public int getWindingRule() {
            return this.pi.getWindingRule();
        }

        @Override
        public boolean isDone() {
            return this.pi.isDone();
        }

        @Override
        public void next() {
            this.pi.next();
        }
    }
}

