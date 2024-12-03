/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.AbstractGraphicsNode;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.util.HaltingThread;

public class ShapeNode
extends AbstractGraphicsNode {
    protected Shape shape;
    protected ShapePainter shapePainter;
    private Rectangle2D primitiveBounds;
    private Rectangle2D geometryBounds;
    private Rectangle2D sensitiveBounds;
    private Shape paintedArea;
    private Shape sensitiveArea;

    public void setShape(Shape newShape) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.shape = newShape;
        if (this.shapePainter != null) {
            if (newShape != null) {
                this.shapePainter.setShape(newShape);
            } else {
                this.shapePainter = null;
            }
        }
        this.fireGraphicsNodeChangeCompleted();
    }

    public Shape getShape() {
        return this.shape;
    }

    public void setShapePainter(ShapePainter newShapePainter) {
        if (this.shape == null) {
            return;
        }
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.shapePainter = newShapePainter;
        if (this.shapePainter != null && this.shape != this.shapePainter.getShape()) {
            this.shapePainter.setShape(this.shape);
        }
        this.fireGraphicsNodeChangeCompleted();
    }

    public ShapePainter getShapePainter() {
        return this.shapePainter;
    }

    @Override
    public void paint(Graphics2D g2d) {
        if (this.isVisible) {
            super.paint(g2d);
        }
    }

    @Override
    public void primitivePaint(Graphics2D g2d) {
        if (this.shapePainter != null) {
            this.shapePainter.paint(g2d);
        }
    }

    @Override
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        this.primitiveBounds = null;
        this.geometryBounds = null;
        this.sensitiveBounds = null;
        this.paintedArea = null;
        this.sensitiveArea = null;
    }

    @Override
    public void setPointerEventType(int pointerEventType) {
        super.setPointerEventType(pointerEventType);
        this.sensitiveBounds = null;
        this.sensitiveArea = null;
    }

    @Override
    public boolean contains(Point2D p) {
        switch (this.pointerEventType) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                if (!this.isVisible) {
                    return false;
                }
            }
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                Rectangle2D b = this.getSensitiveBounds();
                if (b == null || !b.contains(p)) {
                    return false;
                }
                return this.inSensitiveArea(p);
            }
        }
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        Rectangle2D b = this.getBounds();
        if (b != null) {
            return b.intersects(r) && this.paintedArea != null && this.paintedArea.intersects(r);
        }
        return false;
    }

    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (!this.isVisible) {
            return null;
        }
        if (this.shape == null) {
            return null;
        }
        if (this.primitiveBounds != null) {
            return this.primitiveBounds;
        }
        this.primitiveBounds = this.shapePainter == null ? this.shape.getBounds2D() : this.shapePainter.getPaintedBounds2D();
        if (HaltingThread.hasBeenHalted()) {
            this.invalidateGeometryCache();
        }
        return this.primitiveBounds;
    }

    public boolean inSensitiveArea(Point2D pt) {
        if (this.shapePainter == null) {
            return false;
        }
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (this.shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = this.shapePainter;
        } else if (this.shapePainter instanceof FillShapePainter) {
            fillShapePainter = this.shapePainter;
        } else if (this.shapePainter instanceof CompositeShapePainter) {
            CompositeShapePainter cp = (CompositeShapePainter)this.shapePainter;
            for (int i = 0; i < cp.getShapePainterCount(); ++i) {
                ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                    continue;
                }
                if (!(sp instanceof FillShapePainter)) continue;
                fillShapePainter = sp;
            }
        } else {
            return false;
        }
        switch (this.pointerEventType) {
            case 0: 
            case 4: {
                return this.shapePainter.inPaintedArea(pt);
            }
            case 3: 
            case 7: {
                return this.shapePainter.inSensitiveArea(pt);
            }
            case 1: 
            case 5: {
                if (fillShapePainter == null) break;
                return fillShapePainter.inSensitiveArea(pt);
            }
            case 2: 
            case 6: {
                if (strokeShapePainter == null) break;
                return strokeShapePainter.inSensitiveArea(pt);
            }
        }
        return false;
    }

    @Override
    public Rectangle2D getSensitiveBounds() {
        if (this.sensitiveBounds != null) {
            return this.sensitiveBounds;
        }
        if (this.shapePainter == null) {
            return null;
        }
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (this.shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = this.shapePainter;
        } else if (this.shapePainter instanceof FillShapePainter) {
            fillShapePainter = this.shapePainter;
        } else if (this.shapePainter instanceof CompositeShapePainter) {
            CompositeShapePainter cp = (CompositeShapePainter)this.shapePainter;
            for (int i = 0; i < cp.getShapePainterCount(); ++i) {
                ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                    continue;
                }
                if (!(sp instanceof FillShapePainter)) continue;
                fillShapePainter = sp;
            }
        } else {
            return null;
        }
        switch (this.pointerEventType) {
            case 0: 
            case 4: {
                this.sensitiveBounds = this.shapePainter.getPaintedBounds2D();
                break;
            }
            case 1: 
            case 5: {
                if (fillShapePainter == null) break;
                this.sensitiveBounds = fillShapePainter.getSensitiveBounds2D();
                break;
            }
            case 2: 
            case 6: {
                if (strokeShapePainter == null) break;
                this.sensitiveBounds = strokeShapePainter.getSensitiveBounds2D();
                break;
            }
            case 3: 
            case 7: {
                this.sensitiveBounds = this.shapePainter.getSensitiveBounds2D();
                break;
            }
        }
        return this.sensitiveBounds;
    }

    public Shape getSensitiveArea() {
        if (this.sensitiveArea != null) {
            return this.sensitiveArea;
        }
        if (this.shapePainter == null) {
            return null;
        }
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (this.shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = this.shapePainter;
        } else if (this.shapePainter instanceof FillShapePainter) {
            fillShapePainter = this.shapePainter;
        } else if (this.shapePainter instanceof CompositeShapePainter) {
            CompositeShapePainter cp = (CompositeShapePainter)this.shapePainter;
            for (int i = 0; i < cp.getShapePainterCount(); ++i) {
                ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                    continue;
                }
                if (!(sp instanceof FillShapePainter)) continue;
                fillShapePainter = sp;
            }
        } else {
            return null;
        }
        switch (this.pointerEventType) {
            case 0: 
            case 4: {
                this.sensitiveArea = this.shapePainter.getPaintedArea();
                break;
            }
            case 1: 
            case 5: {
                if (fillShapePainter == null) break;
                this.sensitiveArea = fillShapePainter.getSensitiveArea();
                break;
            }
            case 2: 
            case 6: {
                if (strokeShapePainter == null) break;
                this.sensitiveArea = strokeShapePainter.getSensitiveArea();
                break;
            }
            case 3: 
            case 7: {
                this.sensitiveArea = this.shapePainter.getSensitiveArea();
                break;
            }
        }
        return this.sensitiveArea;
    }

    @Override
    public Rectangle2D getGeometryBounds() {
        if (this.geometryBounds == null) {
            if (this.shape == null) {
                return null;
            }
            this.geometryBounds = this.normalizeRectangle(this.shape.getBounds2D());
        }
        return this.geometryBounds;
    }

    @Override
    public Shape getOutline() {
        return this.shape;
    }
}

