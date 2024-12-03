/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot.dial;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.plot.dial.AbstractDialLayer;
import org.jfree.chart.plot.dial.DialLayer;
import org.jfree.chart.plot.dial.DialLayerChangeEvent;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class DialTextAnnotation
extends AbstractDialLayer
implements DialLayer,
Cloneable,
PublicCloneable,
Serializable {
    static final long serialVersionUID = 3065267524054428071L;
    private String label;
    private Font font;
    private transient Paint paint;
    private double angle;
    private double radius;
    private TextAnchor anchor;

    public DialTextAnnotation(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");
        }
        this.angle = -90.0;
        this.radius = 0.3;
        this.font = new Font("Dialog", 1, 14);
        this.paint = Color.black;
        this.label = label;
        this.anchor = TextAnchor.TOP_CENTER;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");
        }
        this.label = label;
        this.notifyListeners(new DialLayerChangeEvent(this));
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.font = font;
        this.notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        this.notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        this.notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        if (radius < 0.0) {
            throw new IllegalArgumentException("The 'radius' cannot be negative.");
        }
        this.radius = radius;
        this.notifyListeners(new DialLayerChangeEvent(this));
    }

    public TextAnchor getAnchor() {
        return this.anchor;
    }

    public void setAnchor(TextAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.anchor = anchor;
        this.notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean isClippedToWindow() {
        return true;
    }

    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius, this.radius);
        Arc2D.Double arc = new Arc2D.Double(f, this.angle, 0.0, 0);
        Point2D pt = arc.getStartPoint();
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        TextUtilities.drawAlignedString(this.label, g2, (float)pt.getX(), (float)pt.getY(), this.anchor);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialTextAnnotation)) {
            return false;
        }
        DialTextAnnotation that = (DialTextAnnotation)obj;
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (this.radius != that.radius) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        if (!this.anchor.equals(that.anchor)) {
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        int result = 193;
        result = 37 * result + HashUtilities.hashCodeForPaint(this.paint);
        result = 37 * result + this.font.hashCode();
        result = 37 * result + this.label.hashCode();
        result = 37 * result + this.anchor.hashCode();
        long temp = Double.doubleToLongBits(this.angle);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.radius);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }
}

