/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.jfree.ui.Size2D;

public final class RectangleAnchor
implements Serializable {
    private static final long serialVersionUID = -2457494205644416327L;
    public static final RectangleAnchor CENTER = new RectangleAnchor("RectangleAnchor.CENTER");
    public static final RectangleAnchor TOP = new RectangleAnchor("RectangleAnchor.TOP");
    public static final RectangleAnchor TOP_LEFT = new RectangleAnchor("RectangleAnchor.TOP_LEFT");
    public static final RectangleAnchor TOP_RIGHT = new RectangleAnchor("RectangleAnchor.TOP_RIGHT");
    public static final RectangleAnchor BOTTOM = new RectangleAnchor("RectangleAnchor.BOTTOM");
    public static final RectangleAnchor BOTTOM_LEFT = new RectangleAnchor("RectangleAnchor.BOTTOM_LEFT");
    public static final RectangleAnchor BOTTOM_RIGHT = new RectangleAnchor("RectangleAnchor.BOTTOM_RIGHT");
    public static final RectangleAnchor LEFT = new RectangleAnchor("RectangleAnchor.LEFT");
    public static final RectangleAnchor RIGHT = new RectangleAnchor("RectangleAnchor.RIGHT");
    private String name;

    private RectangleAnchor(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RectangleAnchor)) {
            return false;
        }
        RectangleAnchor order = (RectangleAnchor)obj;
        return this.name.equals(order.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public static Point2D coordinates(Rectangle2D rectangle, RectangleAnchor anchor) {
        Point2D.Double result = new Point2D.Double();
        if (anchor == CENTER) {
            ((Point2D)result).setLocation(rectangle.getCenterX(), rectangle.getCenterY());
        } else if (anchor == TOP) {
            ((Point2D)result).setLocation(rectangle.getCenterX(), rectangle.getMinY());
        } else if (anchor == BOTTOM) {
            ((Point2D)result).setLocation(rectangle.getCenterX(), rectangle.getMaxY());
        } else if (anchor == LEFT) {
            ((Point2D)result).setLocation(rectangle.getMinX(), rectangle.getCenterY());
        } else if (anchor == RIGHT) {
            ((Point2D)result).setLocation(rectangle.getMaxX(), rectangle.getCenterY());
        } else if (anchor == TOP_LEFT) {
            ((Point2D)result).setLocation(rectangle.getMinX(), rectangle.getMinY());
        } else if (anchor == TOP_RIGHT) {
            ((Point2D)result).setLocation(rectangle.getMaxX(), rectangle.getMinY());
        } else if (anchor == BOTTOM_LEFT) {
            ((Point2D)result).setLocation(rectangle.getMinX(), rectangle.getMaxY());
        } else if (anchor == BOTTOM_RIGHT) {
            ((Point2D)result).setLocation(rectangle.getMaxX(), rectangle.getMaxY());
        }
        return result;
    }

    public static Rectangle2D createRectangle(Size2D dimensions, double anchorX, double anchorY, RectangleAnchor anchor) {
        Rectangle2D.Double result = null;
        double w = dimensions.getWidth();
        double h = dimensions.getHeight();
        if (anchor == CENTER) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        } else if (anchor == TOP) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        } else if (anchor == BOTTOM) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        } else if (anchor == LEFT) {
            result = new Rectangle2D.Double(anchorX, anchorY - h / 2.0, w, h);
        } else if (anchor == RIGHT) {
            result = new Rectangle2D.Double(anchorX - w, anchorY - h / 2.0, w, h);
        } else if (anchor == TOP_LEFT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        } else if (anchor == TOP_RIGHT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        } else if (anchor == BOTTOM_LEFT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        } else if (anchor == BOTTOM_RIGHT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        return result;
    }

    private Object readResolve() throws ObjectStreamException {
        RectangleAnchor result = null;
        if (this.equals(CENTER)) {
            result = CENTER;
        } else if (this.equals(TOP)) {
            result = TOP;
        } else if (this.equals(BOTTOM)) {
            result = BOTTOM;
        } else if (this.equals(LEFT)) {
            result = LEFT;
        } else if (this.equals(RIGHT)) {
            result = RIGHT;
        } else if (this.equals(TOP_LEFT)) {
            result = TOP_LEFT;
        } else if (this.equals(TOP_RIGHT)) {
            result = TOP_RIGHT;
        } else if (this.equals(BOTTOM_LEFT)) {
            result = BOTTOM_LEFT;
        } else if (this.equals(BOTTOM_RIGHT)) {
            result = BOTTOM_RIGHT;
        }
        return result;
    }
}

