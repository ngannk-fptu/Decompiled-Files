/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.ShapePainter;

public class StrokeShapePainter
implements ShapePainter {
    protected Shape shape;
    protected Shape strokedShape;
    protected Stroke stroke;
    protected Paint paint;

    public StrokeShapePainter(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    public void setStroke(Stroke newStroke) {
        this.stroke = newStroke;
        this.strokedShape = null;
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void setPaint(Paint newPaint) {
        this.paint = newPaint;
    }

    public Paint getPaint() {
        return this.paint;
    }

    @Override
    public void paint(Graphics2D g2d) {
        if (this.stroke != null && this.paint != null) {
            g2d.setPaint(this.paint);
            g2d.setStroke(this.stroke);
            g2d.draw(this.shape);
        }
    }

    @Override
    public Shape getPaintedArea() {
        if (this.paint == null || this.stroke == null) {
            return null;
        }
        if (this.strokedShape == null) {
            this.strokedShape = this.stroke.createStrokedShape(this.shape);
        }
        return this.strokedShape;
    }

    @Override
    public Rectangle2D getPaintedBounds2D() {
        Shape painted = this.getPaintedArea();
        if (painted == null) {
            return null;
        }
        return painted.getBounds2D();
    }

    @Override
    public boolean inPaintedArea(Point2D pt) {
        Shape painted = this.getPaintedArea();
        if (painted == null) {
            return false;
        }
        return painted.contains(pt);
    }

    @Override
    public Shape getSensitiveArea() {
        if (this.stroke == null) {
            return null;
        }
        if (this.strokedShape == null) {
            this.strokedShape = this.stroke.createStrokedShape(this.shape);
        }
        return this.strokedShape;
    }

    @Override
    public Rectangle2D getSensitiveBounds2D() {
        Shape sensitive = this.getSensitiveArea();
        if (sensitive == null) {
            return null;
        }
        return sensitive.getBounds2D();
    }

    @Override
    public boolean inSensitiveArea(Point2D pt) {
        Shape sensitive = this.getSensitiveArea();
        if (sensitive == null) {
            return false;
        }
        return sensitive.contains(pt);
    }

    @Override
    public void setShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
        this.strokedShape = null;
    }

    @Override
    public Shape getShape() {
        return this.shape;
    }
}

