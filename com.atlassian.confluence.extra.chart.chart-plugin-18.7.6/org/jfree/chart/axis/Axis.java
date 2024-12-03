/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.entity.AxisEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public abstract class Axis
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 7719289504573298271L;
    public static final boolean DEFAULT_AXIS_VISIBLE = true;
    public static final Font DEFAULT_AXIS_LABEL_FONT = new Font("SansSerif", 0, 12);
    public static final Paint DEFAULT_AXIS_LABEL_PAINT = Color.black;
    public static final RectangleInsets DEFAULT_AXIS_LABEL_INSETS = new RectangleInsets(3.0, 3.0, 3.0, 3.0);
    public static final Paint DEFAULT_AXIS_LINE_PAINT = Color.gray;
    public static final Stroke DEFAULT_AXIS_LINE_STROKE = new BasicStroke(1.0f);
    public static final boolean DEFAULT_TICK_LABELS_VISIBLE = true;
    public static final Font DEFAULT_TICK_LABEL_FONT = new Font("SansSerif", 0, 10);
    public static final Paint DEFAULT_TICK_LABEL_PAINT = Color.black;
    public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS = new RectangleInsets(2.0, 4.0, 2.0, 4.0);
    public static final boolean DEFAULT_TICK_MARKS_VISIBLE = true;
    public static final Stroke DEFAULT_TICK_MARK_STROKE = new BasicStroke(1.0f);
    public static final Paint DEFAULT_TICK_MARK_PAINT = Color.gray;
    public static final float DEFAULT_TICK_MARK_INSIDE_LENGTH = 0.0f;
    public static final float DEFAULT_TICK_MARK_OUTSIDE_LENGTH = 2.0f;
    private boolean visible;
    private String label;
    private Font labelFont;
    private transient Paint labelPaint;
    private RectangleInsets labelInsets;
    private double labelAngle;
    private boolean axisLineVisible;
    private transient Stroke axisLineStroke;
    private transient Paint axisLinePaint;
    private boolean tickLabelsVisible;
    private Font tickLabelFont;
    private transient Paint tickLabelPaint;
    private RectangleInsets tickLabelInsets;
    private boolean tickMarksVisible;
    private float tickMarkInsideLength;
    private float tickMarkOutsideLength;
    private boolean minorTickMarksVisible;
    private float minorTickMarkInsideLength;
    private float minorTickMarkOutsideLength;
    private transient Stroke tickMarkStroke;
    private transient Paint tickMarkPaint;
    private double fixedDimension;
    private transient Plot plot;
    private transient EventListenerList listenerList;
    static /* synthetic */ Class class$org$jfree$chart$event$AxisChangeListener;

    protected Axis(String label) {
        this.label = label;
        this.visible = true;
        this.labelFont = DEFAULT_AXIS_LABEL_FONT;
        this.labelPaint = DEFAULT_AXIS_LABEL_PAINT;
        this.labelInsets = DEFAULT_AXIS_LABEL_INSETS;
        this.labelAngle = 0.0;
        this.axisLineVisible = true;
        this.axisLinePaint = DEFAULT_AXIS_LINE_PAINT;
        this.axisLineStroke = DEFAULT_AXIS_LINE_STROKE;
        this.tickLabelsVisible = true;
        this.tickLabelFont = DEFAULT_TICK_LABEL_FONT;
        this.tickLabelPaint = DEFAULT_TICK_LABEL_PAINT;
        this.tickLabelInsets = DEFAULT_TICK_LABEL_INSETS;
        this.tickMarksVisible = true;
        this.tickMarkStroke = DEFAULT_TICK_MARK_STROKE;
        this.tickMarkPaint = DEFAULT_TICK_MARK_PAINT;
        this.tickMarkInsideLength = 0.0f;
        this.tickMarkOutsideLength = 2.0f;
        this.minorTickMarksVisible = false;
        this.minorTickMarkInsideLength = 0.0f;
        this.minorTickMarkOutsideLength = 2.0f;
        this.plot = null;
        this.listenerList = new EventListenerList();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean flag) {
        if (flag != this.visible) {
            this.visible = flag;
            this.fireChangeEvent();
        }
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        String existing = this.label;
        if (existing != null) {
            if (!existing.equals(label)) {
                this.label = label;
                this.fireChangeEvent();
            }
        } else if (label != null) {
            this.label = label;
            this.fireChangeEvent();
        }
    }

    public Font getLabelFont() {
        return this.labelFont;
    }

    public void setLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            this.fireChangeEvent();
        }
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelPaint = paint;
        this.fireChangeEvent();
    }

    public RectangleInsets getLabelInsets() {
        return this.labelInsets;
    }

    public void setLabelInsets(RectangleInsets insets) {
        this.setLabelInsets(insets, true);
    }

    public void setLabelInsets(RectangleInsets insets, boolean notify) {
        if (insets == null) {
            throw new IllegalArgumentException("Null 'insets' argument.");
        }
        if (!insets.equals(this.labelInsets)) {
            this.labelInsets = insets;
            if (notify) {
                this.fireChangeEvent();
            }
        }
    }

    public double getLabelAngle() {
        return this.labelAngle;
    }

    public void setLabelAngle(double angle) {
        this.labelAngle = angle;
        this.fireChangeEvent();
    }

    public boolean isAxisLineVisible() {
        return this.axisLineVisible;
    }

    public void setAxisLineVisible(boolean visible) {
        this.axisLineVisible = visible;
        this.fireChangeEvent();
    }

    public Paint getAxisLinePaint() {
        return this.axisLinePaint;
    }

    public void setAxisLinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.axisLinePaint = paint;
        this.fireChangeEvent();
    }

    public Stroke getAxisLineStroke() {
        return this.axisLineStroke;
    }

    public void setAxisLineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.axisLineStroke = stroke;
        this.fireChangeEvent();
    }

    public boolean isTickLabelsVisible() {
        return this.tickLabelsVisible;
    }

    public void setTickLabelsVisible(boolean flag) {
        if (flag != this.tickLabelsVisible) {
            this.tickLabelsVisible = flag;
            this.fireChangeEvent();
        }
    }

    public boolean isMinorTickMarksVisible() {
        return this.minorTickMarksVisible;
    }

    public void setMinorTickMarksVisible(boolean flag) {
        if (flag != this.minorTickMarksVisible) {
            this.minorTickMarksVisible = flag;
            this.fireChangeEvent();
        }
    }

    public Font getTickLabelFont() {
        return this.tickLabelFont;
    }

    public void setTickLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        if (!this.tickLabelFont.equals(font)) {
            this.tickLabelFont = font;
            this.fireChangeEvent();
        }
    }

    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    public void setTickLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.tickLabelPaint = paint;
        this.fireChangeEvent();
    }

    public RectangleInsets getTickLabelInsets() {
        return this.tickLabelInsets;
    }

    public void setTickLabelInsets(RectangleInsets insets) {
        if (insets == null) {
            throw new IllegalArgumentException("Null 'insets' argument.");
        }
        if (!this.tickLabelInsets.equals(insets)) {
            this.tickLabelInsets = insets;
            this.fireChangeEvent();
        }
    }

    public boolean isTickMarksVisible() {
        return this.tickMarksVisible;
    }

    public void setTickMarksVisible(boolean flag) {
        if (flag != this.tickMarksVisible) {
            this.tickMarksVisible = flag;
            this.fireChangeEvent();
        }
    }

    public float getTickMarkInsideLength() {
        return this.tickMarkInsideLength;
    }

    public void setTickMarkInsideLength(float length) {
        this.tickMarkInsideLength = length;
        this.fireChangeEvent();
    }

    public float getTickMarkOutsideLength() {
        return this.tickMarkOutsideLength;
    }

    public void setTickMarkOutsideLength(float length) {
        this.tickMarkOutsideLength = length;
        this.fireChangeEvent();
    }

    public Stroke getTickMarkStroke() {
        return this.tickMarkStroke;
    }

    public void setTickMarkStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        if (!this.tickMarkStroke.equals(stroke)) {
            this.tickMarkStroke = stroke;
            this.fireChangeEvent();
        }
    }

    public Paint getTickMarkPaint() {
        return this.tickMarkPaint;
    }

    public void setTickMarkPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.tickMarkPaint = paint;
        this.fireChangeEvent();
    }

    public float getMinorTickMarkInsideLength() {
        return this.minorTickMarkInsideLength;
    }

    public void setMinorTickMarkInsideLength(float length) {
        this.minorTickMarkInsideLength = length;
        this.fireChangeEvent();
    }

    public float getMinorTickMarkOutsideLength() {
        return this.minorTickMarkOutsideLength;
    }

    public void setMinorTickMarkOutsideLength(float length) {
        this.minorTickMarkOutsideLength = length;
        this.fireChangeEvent();
    }

    public Plot getPlot() {
        return this.plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
        this.configure();
    }

    public double getFixedDimension() {
        return this.fixedDimension;
    }

    public void setFixedDimension(double dimension) {
        this.fixedDimension = dimension;
    }

    public abstract void configure();

    public abstract AxisSpace reserveSpace(Graphics2D var1, Plot var2, Rectangle2D var3, RectangleEdge var4, AxisSpace var5);

    public abstract AxisState draw(Graphics2D var1, double var2, Rectangle2D var4, Rectangle2D var5, RectangleEdge var6, PlotRenderingInfo var7);

    public abstract List refreshTicks(Graphics2D var1, AxisState var2, Rectangle2D var3, RectangleEdge var4);

    protected void createAndAddEntity(double cursor, AxisState state, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (plotState == null || plotState.getOwner() == null) {
            return;
        }
        Rectangle2D.Double hotspot = null;
        if (edge.equals(RectangleEdge.TOP)) {
            hotspot = new Rectangle2D.Double(dataArea.getX(), state.getCursor(), dataArea.getWidth(), cursor - state.getCursor());
        } else if (edge.equals(RectangleEdge.BOTTOM)) {
            hotspot = new Rectangle2D.Double(dataArea.getX(), cursor, dataArea.getWidth(), state.getCursor() - cursor);
        } else if (edge.equals(RectangleEdge.LEFT)) {
            hotspot = new Rectangle2D.Double(state.getCursor(), dataArea.getY(), cursor - state.getCursor(), dataArea.getHeight());
        } else if (edge.equals(RectangleEdge.RIGHT)) {
            hotspot = new Rectangle2D.Double(cursor, dataArea.getY(), state.getCursor() - cursor, dataArea.getHeight());
        }
        EntityCollection e = plotState.getOwner().getEntityCollection();
        if (e != null) {
            e.add(new AxisEntity((Shape)hotspot, this));
        }
    }

    public void addChangeListener(AxisChangeListener listener) {
        this.listenerList.add(class$org$jfree$chart$event$AxisChangeListener == null ? (class$org$jfree$chart$event$AxisChangeListener = Axis.class$("org.jfree.chart.event.AxisChangeListener")) : class$org$jfree$chart$event$AxisChangeListener, listener);
    }

    public void removeChangeListener(AxisChangeListener listener) {
        this.listenerList.remove(class$org$jfree$chart$event$AxisChangeListener == null ? (class$org$jfree$chart$event$AxisChangeListener = Axis.class$("org.jfree.chart.event.AxisChangeListener")) : class$org$jfree$chart$event$AxisChangeListener, listener);
    }

    public boolean hasListener(EventListener listener) {
        List<Object> list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    protected void notifyListeners(AxisChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$org$jfree$chart$event$AxisChangeListener == null ? Axis.class$("org.jfree.chart.event.AxisChangeListener") : class$org$jfree$chart$event$AxisChangeListener)) continue;
            ((AxisChangeListener)listeners[i + 1]).axisChanged(event);
        }
    }

    protected void fireChangeEvent() {
        this.notifyListeners(new AxisChangeEvent(this));
    }

    protected Rectangle2D getLabelEnclosure(Graphics2D g2, RectangleEdge edge) {
        Rectangle2D result = new Rectangle2D.Double();
        String axisLabel = this.getLabel();
        if (axisLabel != null && !axisLabel.equals("")) {
            FontMetrics fm = g2.getFontMetrics(this.getLabelFont());
            Rectangle2D bounds = TextUtilities.getTextBounds(axisLabel, g2, fm);
            RectangleInsets insets = this.getLabelInsets();
            bounds = insets.createOutsetRectangle(bounds);
            double angle = this.getLabelAngle();
            if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                angle -= 1.5707963267948966;
            }
            double x = bounds.getCenterX();
            double y = bounds.getCenterY();
            AffineTransform transformer = AffineTransform.getRotateInstance(angle, x, y);
            Shape labelBounds = transformer.createTransformedShape(bounds);
            result = labelBounds.getBounds2D();
        }
        return result;
    }

    protected AxisState drawLabel(String label, Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state) {
        if (state == null) {
            throw new IllegalArgumentException("Null 'state' argument.");
        }
        if (label == null || label.equals("")) {
            return state;
        }
        Font font = this.getLabelFont();
        RectangleInsets insets = this.getLabelInsets();
        g2.setFont(font);
        g2.setPaint(this.getLabelPaint());
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D labelBounds = TextUtilities.getTextBounds(label, g2, fm);
        if (edge == RectangleEdge.TOP) {
            AffineTransform t = AffineTransform.getRotateInstance(this.getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = dataArea.getCenterX();
            double labely = state.getCursor() - insets.getBottom() - labelBounds.getHeight() / 2.0;
            TextUtilities.drawRotatedString(label, g2, (float)labelx, (float)labely, TextAnchor.CENTER, this.getLabelAngle(), TextAnchor.CENTER);
            state.cursorUp(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
        } else if (edge == RectangleEdge.BOTTOM) {
            AffineTransform t = AffineTransform.getRotateInstance(this.getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = dataArea.getCenterX();
            double labely = state.getCursor() + insets.getTop() + labelBounds.getHeight() / 2.0;
            TextUtilities.drawRotatedString(label, g2, (float)labelx, (float)labely, TextAnchor.CENTER, this.getLabelAngle(), TextAnchor.CENTER);
            state.cursorDown(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
        } else if (edge == RectangleEdge.LEFT) {
            AffineTransform t = AffineTransform.getRotateInstance(this.getLabelAngle() - 1.5707963267948966, labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = state.getCursor() - insets.getRight() - labelBounds.getWidth() / 2.0;
            double labely = dataArea.getCenterY();
            TextUtilities.drawRotatedString(label, g2, (float)labelx, (float)labely, TextAnchor.CENTER, this.getLabelAngle() - 1.5707963267948966, TextAnchor.CENTER);
            state.cursorLeft(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
        } else if (edge == RectangleEdge.RIGHT) {
            AffineTransform t = AffineTransform.getRotateInstance(this.getLabelAngle() + 1.5707963267948966, labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = state.getCursor() + insets.getLeft() + labelBounds.getWidth() / 2.0;
            double labely = dataArea.getY() + dataArea.getHeight() / 2.0;
            TextUtilities.drawRotatedString(label, g2, (float)labelx, (float)labely, TextAnchor.CENTER, this.getLabelAngle() + 1.5707963267948966, TextAnchor.CENTER);
            state.cursorRight(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
        }
        return state;
    }

    protected void drawAxisLine(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        Line2D.Double axisLine = null;
        if (edge == RectangleEdge.TOP) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.BOTTOM) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.LEFT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());
        } else if (edge == RectangleEdge.RIGHT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());
        }
        g2.setPaint(this.axisLinePaint);
        g2.setStroke(this.axisLineStroke);
        g2.draw(axisLine);
    }

    public Object clone() throws CloneNotSupportedException {
        Axis clone = (Axis)super.clone();
        clone.plot = null;
        clone.listenerList = new EventListenerList();
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Axis)) {
            return false;
        }
        Axis that = (Axis)obj;
        if (this.visible != that.visible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.label, that.label)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelInsets, that.labelInsets)) {
            return false;
        }
        if (this.labelAngle != that.labelAngle) {
            return false;
        }
        if (this.axisLineVisible != that.axisLineVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.axisLineStroke, that.axisLineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.axisLinePaint, that.axisLinePaint)) {
            return false;
        }
        if (this.tickLabelsVisible != that.tickLabelsVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickLabelFont, that.tickLabelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickLabelPaint, that.tickLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickLabelInsets, that.tickLabelInsets)) {
            return false;
        }
        if (this.tickMarksVisible != that.tickMarksVisible) {
            return false;
        }
        if (this.tickMarkInsideLength != that.tickMarkInsideLength) {
            return false;
        }
        if (this.tickMarkOutsideLength != that.tickMarkOutsideLength) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickMarkPaint, that.tickMarkPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickMarkStroke, that.tickMarkStroke)) {
            return false;
        }
        if (this.minorTickMarksVisible != that.minorTickMarksVisible) {
            return false;
        }
        if (this.minorTickMarkInsideLength != that.minorTickMarkInsideLength) {
            return false;
        }
        if (this.minorTickMarkOutsideLength != that.minorTickMarkOutsideLength) {
            return false;
        }
        return this.fixedDimension == that.fixedDimension;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.tickLabelPaint, stream);
        SerialUtilities.writeStroke(this.axisLineStroke, stream);
        SerialUtilities.writePaint(this.axisLinePaint, stream);
        SerialUtilities.writeStroke(this.tickMarkStroke, stream);
        SerialUtilities.writePaint(this.tickMarkPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.tickLabelPaint = SerialUtilities.readPaint(stream);
        this.axisLineStroke = SerialUtilities.readStroke(stream);
        this.axisLinePaint = SerialUtilities.readPaint(stream);
        this.tickMarkStroke = SerialUtilities.readStroke(stream);
        this.tickMarkPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

