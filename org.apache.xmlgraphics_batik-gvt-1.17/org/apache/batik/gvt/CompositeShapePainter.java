/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.ShapePainter;

public class CompositeShapePainter
implements ShapePainter {
    protected Shape shape;
    protected ShapePainter[] painters;
    protected int count;

    public CompositeShapePainter(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    public void addShapePainter(ShapePainter shapePainter) {
        if (shapePainter == null) {
            return;
        }
        if (this.shape != shapePainter.getShape()) {
            shapePainter.setShape(this.shape);
        }
        if (this.painters == null) {
            this.painters = new ShapePainter[2];
        }
        if (this.count == this.painters.length) {
            ShapePainter[] newPainters = new ShapePainter[this.count + this.count / 2 + 1];
            System.arraycopy(this.painters, 0, newPainters, 0, this.count);
            this.painters = newPainters;
        }
        this.painters[this.count++] = shapePainter;
    }

    public ShapePainter getShapePainter(int index) {
        return this.painters[index];
    }

    public int getShapePainterCount() {
        return this.count;
    }

    @Override
    public void paint(Graphics2D g2d) {
        if (this.painters != null) {
            for (int i = 0; i < this.count; ++i) {
                this.painters[i].paint(g2d);
            }
        }
    }

    @Override
    public Shape getPaintedArea() {
        if (this.painters == null) {
            return null;
        }
        Area paintedArea = new Area();
        for (int i = 0; i < this.count; ++i) {
            Shape s = this.painters[i].getPaintedArea();
            if (s == null) continue;
            paintedArea.add(new Area(s));
        }
        return paintedArea;
    }

    @Override
    public Rectangle2D getPaintedBounds2D() {
        if (this.painters == null) {
            return null;
        }
        Rectangle2D bounds = null;
        for (int i = 0; i < this.count; ++i) {
            Rectangle2D pb = this.painters[i].getPaintedBounds2D();
            if (pb == null) continue;
            if (bounds == null) {
                bounds = (Rectangle2D)pb.clone();
                continue;
            }
            bounds.add(pb);
        }
        return bounds;
    }

    @Override
    public boolean inPaintedArea(Point2D pt) {
        if (this.painters == null) {
            return false;
        }
        for (int i = 0; i < this.count; ++i) {
            if (!this.painters[i].inPaintedArea(pt)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Shape getSensitiveArea() {
        if (this.painters == null) {
            return null;
        }
        Area paintedArea = new Area();
        for (int i = 0; i < this.count; ++i) {
            Shape s = this.painters[i].getSensitiveArea();
            if (s == null) continue;
            paintedArea.add(new Area(s));
        }
        return paintedArea;
    }

    @Override
    public Rectangle2D getSensitiveBounds2D() {
        if (this.painters == null) {
            return null;
        }
        Rectangle2D bounds = null;
        for (int i = 0; i < this.count; ++i) {
            Rectangle2D pb = this.painters[i].getSensitiveBounds2D();
            if (pb == null) continue;
            if (bounds == null) {
                bounds = (Rectangle2D)pb.clone();
                continue;
            }
            bounds.add(pb);
        }
        return bounds;
    }

    @Override
    public boolean inSensitiveArea(Point2D pt) {
        if (this.painters == null) {
            return false;
        }
        for (int i = 0; i < this.count; ++i) {
            if (!this.painters[i].inSensitiveArea(pt)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        if (this.painters != null) {
            for (int i = 0; i < this.count; ++i) {
                this.painters[i].setShape(shape);
            }
        }
        this.shape = shape;
    }

    @Override
    public Shape getShape() {
        return this.shape;
    }
}

