/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.jfree.chart.ChartColor;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class DefaultDrawingSupplier
implements DrawingSupplier,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -7339847061039422538L;
    public static final Paint[] DEFAULT_PAINT_SEQUENCE = ChartColor.createDefaultPaintArray();
    public static final Paint[] DEFAULT_OUTLINE_PAINT_SEQUENCE = new Paint[]{Color.lightGray};
    public static final Paint[] DEFAULT_FILL_PAINT_SEQUENCE = new Paint[]{Color.white};
    public static final Stroke[] DEFAULT_STROKE_SEQUENCE = new Stroke[]{new BasicStroke(1.0f, 2, 2)};
    public static final Stroke[] DEFAULT_OUTLINE_STROKE_SEQUENCE = new Stroke[]{new BasicStroke(1.0f, 2, 2)};
    public static final Shape[] DEFAULT_SHAPE_SEQUENCE = DefaultDrawingSupplier.createStandardSeriesShapes();
    private transient Paint[] paintSequence;
    private int paintIndex;
    private transient Paint[] outlinePaintSequence;
    private int outlinePaintIndex;
    private transient Paint[] fillPaintSequence;
    private int fillPaintIndex;
    private transient Stroke[] strokeSequence;
    private int strokeIndex;
    private transient Stroke[] outlineStrokeSequence;
    private int outlineStrokeIndex;
    private transient Shape[] shapeSequence;
    private int shapeIndex;

    public DefaultDrawingSupplier() {
        this(DEFAULT_PAINT_SEQUENCE, DEFAULT_FILL_PAINT_SEQUENCE, DEFAULT_OUTLINE_PAINT_SEQUENCE, DEFAULT_STROKE_SEQUENCE, DEFAULT_OUTLINE_STROKE_SEQUENCE, DEFAULT_SHAPE_SEQUENCE);
    }

    public DefaultDrawingSupplier(Paint[] paintSequence, Paint[] outlinePaintSequence, Stroke[] strokeSequence, Stroke[] outlineStrokeSequence, Shape[] shapeSequence) {
        this.paintSequence = paintSequence;
        this.fillPaintSequence = DEFAULT_FILL_PAINT_SEQUENCE;
        this.outlinePaintSequence = outlinePaintSequence;
        this.strokeSequence = strokeSequence;
        this.outlineStrokeSequence = outlineStrokeSequence;
        this.shapeSequence = shapeSequence;
    }

    public DefaultDrawingSupplier(Paint[] paintSequence, Paint[] fillPaintSequence, Paint[] outlinePaintSequence, Stroke[] strokeSequence, Stroke[] outlineStrokeSequence, Shape[] shapeSequence) {
        this.paintSequence = paintSequence;
        this.fillPaintSequence = fillPaintSequence;
        this.outlinePaintSequence = outlinePaintSequence;
        this.strokeSequence = strokeSequence;
        this.outlineStrokeSequence = outlineStrokeSequence;
        this.shapeSequence = shapeSequence;
    }

    public Paint getNextPaint() {
        Paint result = this.paintSequence[this.paintIndex % this.paintSequence.length];
        ++this.paintIndex;
        return result;
    }

    public Paint getNextOutlinePaint() {
        Paint result = this.outlinePaintSequence[this.outlinePaintIndex % this.outlinePaintSequence.length];
        ++this.outlinePaintIndex;
        return result;
    }

    public Paint getNextFillPaint() {
        Paint result = this.fillPaintSequence[this.fillPaintIndex % this.fillPaintSequence.length];
        ++this.fillPaintIndex;
        return result;
    }

    public Stroke getNextStroke() {
        Stroke result = this.strokeSequence[this.strokeIndex % this.strokeSequence.length];
        ++this.strokeIndex;
        return result;
    }

    public Stroke getNextOutlineStroke() {
        Stroke result = this.outlineStrokeSequence[this.outlineStrokeIndex % this.outlineStrokeSequence.length];
        ++this.outlineStrokeIndex;
        return result;
    }

    public Shape getNextShape() {
        Shape result = this.shapeSequence[this.shapeIndex % this.shapeSequence.length];
        ++this.shapeIndex;
        return result;
    }

    public static Shape[] createStandardSeriesShapes() {
        Shape[] result = new Shape[10];
        double size = 6.0;
        double delta = size / 2.0;
        int[] xpoints = null;
        int[] ypoints = null;
        result[0] = new Rectangle2D.Double(-delta, -delta, size, size);
        result[1] = new Ellipse2D.Double(-delta, -delta, size, size);
        xpoints = DefaultDrawingSupplier.intArray(0.0, delta, -delta);
        ypoints = DefaultDrawingSupplier.intArray(-delta, delta, delta);
        result[2] = new Polygon(xpoints, ypoints, 3);
        xpoints = DefaultDrawingSupplier.intArray(0.0, delta, 0.0, -delta);
        ypoints = DefaultDrawingSupplier.intArray(-delta, 0.0, delta, 0.0);
        result[3] = new Polygon(xpoints, ypoints, 4);
        result[4] = new Rectangle2D.Double(-delta, -delta / 2.0, size, size / 2.0);
        xpoints = DefaultDrawingSupplier.intArray(-delta, delta, 0.0);
        ypoints = DefaultDrawingSupplier.intArray(-delta, -delta, delta);
        result[5] = new Polygon(xpoints, ypoints, 3);
        result[6] = new Ellipse2D.Double(-delta, -delta / 2.0, size, size / 2.0);
        xpoints = DefaultDrawingSupplier.intArray(-delta, delta, -delta);
        ypoints = DefaultDrawingSupplier.intArray(-delta, 0.0, delta);
        result[7] = new Polygon(xpoints, ypoints, 3);
        result[8] = new Rectangle2D.Double(-delta / 2.0, -delta, size / 2.0, size);
        xpoints = DefaultDrawingSupplier.intArray(-delta, delta, delta);
        ypoints = DefaultDrawingSupplier.intArray(0.0, -delta, delta);
        result[9] = new Polygon(xpoints, ypoints, 3);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultDrawingSupplier)) {
            return false;
        }
        DefaultDrawingSupplier that = (DefaultDrawingSupplier)obj;
        if (!Arrays.equals(this.paintSequence, that.paintSequence)) {
            return false;
        }
        if (this.paintIndex != that.paintIndex) {
            return false;
        }
        if (!Arrays.equals(this.outlinePaintSequence, that.outlinePaintSequence)) {
            return false;
        }
        if (this.outlinePaintIndex != that.outlinePaintIndex) {
            return false;
        }
        if (!Arrays.equals(this.strokeSequence, that.strokeSequence)) {
            return false;
        }
        if (this.strokeIndex != that.strokeIndex) {
            return false;
        }
        if (!Arrays.equals(this.outlineStrokeSequence, that.outlineStrokeSequence)) {
            return false;
        }
        if (this.outlineStrokeIndex != that.outlineStrokeIndex) {
            return false;
        }
        if (!this.equalShapes(this.shapeSequence, that.shapeSequence)) {
            return false;
        }
        return this.shapeIndex == that.shapeIndex;
    }

    private boolean equalShapes(Shape[] s1, Shape[] s2) {
        if (s1 == null) {
            return s2 == null;
        }
        if (s2 == null) {
            return false;
        }
        if (s1.length != s2.length) {
            return false;
        }
        for (int i = 0; i < s1.length; ++i) {
            if (ShapeUtilities.equal(s1[i], s2[i])) continue;
            return false;
        }
        return true;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int paintCount = this.paintSequence.length;
        stream.writeInt(paintCount);
        for (int i = 0; i < paintCount; ++i) {
            SerialUtilities.writePaint(this.paintSequence[i], stream);
        }
        int outlinePaintCount = this.outlinePaintSequence.length;
        stream.writeInt(outlinePaintCount);
        for (int i = 0; i < outlinePaintCount; ++i) {
            SerialUtilities.writePaint(this.outlinePaintSequence[i], stream);
        }
        int strokeCount = this.strokeSequence.length;
        stream.writeInt(strokeCount);
        for (int i = 0; i < strokeCount; ++i) {
            SerialUtilities.writeStroke(this.strokeSequence[i], stream);
        }
        int outlineStrokeCount = this.outlineStrokeSequence.length;
        stream.writeInt(outlineStrokeCount);
        for (int i = 0; i < outlineStrokeCount; ++i) {
            SerialUtilities.writeStroke(this.outlineStrokeSequence[i], stream);
        }
        int shapeCount = this.shapeSequence.length;
        stream.writeInt(shapeCount);
        for (int i = 0; i < shapeCount; ++i) {
            SerialUtilities.writeShape(this.shapeSequence[i], stream);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int paintCount = stream.readInt();
        this.paintSequence = new Paint[paintCount];
        for (int i = 0; i < paintCount; ++i) {
            this.paintSequence[i] = SerialUtilities.readPaint(stream);
        }
        int outlinePaintCount = stream.readInt();
        this.outlinePaintSequence = new Paint[outlinePaintCount];
        for (int i = 0; i < outlinePaintCount; ++i) {
            this.outlinePaintSequence[i] = SerialUtilities.readPaint(stream);
        }
        int strokeCount = stream.readInt();
        this.strokeSequence = new Stroke[strokeCount];
        for (int i = 0; i < strokeCount; ++i) {
            this.strokeSequence[i] = SerialUtilities.readStroke(stream);
        }
        int outlineStrokeCount = stream.readInt();
        this.outlineStrokeSequence = new Stroke[outlineStrokeCount];
        for (int i = 0; i < outlineStrokeCount; ++i) {
            this.outlineStrokeSequence[i] = SerialUtilities.readStroke(stream);
        }
        int shapeCount = stream.readInt();
        this.shapeSequence = new Shape[shapeCount];
        for (int i = 0; i < shapeCount; ++i) {
            this.shapeSequence[i] = SerialUtilities.readShape(stream);
        }
    }

    private static int[] intArray(double a, double b, double c) {
        return new int[]{(int)a, (int)b, (int)c};
    }

    private static int[] intArray(double a, double b, double c, double d) {
        return new int[]{(int)a, (int)b, (int)c, (int)d};
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultDrawingSupplier clone = (DefaultDrawingSupplier)super.clone();
        return clone;
    }
}

