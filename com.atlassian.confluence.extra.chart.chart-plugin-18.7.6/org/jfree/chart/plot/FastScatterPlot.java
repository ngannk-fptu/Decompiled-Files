/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class FastScatterPlot
extends Plot
implements ValueAxisPlot,
Pannable,
Zoomable,
Cloneable,
Serializable {
    private static final long serialVersionUID = 7871545897358563521L;
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, 0, 2, 0.0f, new float[]{2.0f, 2.0f}, 0.0f);
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;
    private float[][] data;
    private Range xDataRange;
    private Range yDataRange;
    private ValueAxis domainAxis;
    private ValueAxis rangeAxis;
    private transient Paint paint;
    private boolean domainGridlinesVisible;
    private transient Stroke domainGridlineStroke;
    private transient Paint domainGridlinePaint;
    private boolean rangeGridlinesVisible;
    private transient Stroke rangeGridlineStroke;
    private transient Paint rangeGridlinePaint;
    private boolean domainPannable;
    private boolean rangePannable;
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");

    public FastScatterPlot() {
        this(null, new NumberAxis("X"), new NumberAxis("Y"));
    }

    public FastScatterPlot(float[][] data, ValueAxis domainAxis, ValueAxis rangeAxis) {
        if (domainAxis == null) {
            throw new IllegalArgumentException("Null 'domainAxis' argument.");
        }
        if (rangeAxis == null) {
            throw new IllegalArgumentException("Null 'rangeAxis' argument.");
        }
        this.data = data;
        this.xDataRange = this.calculateXDataRange(data);
        this.yDataRange = this.calculateYDataRange(data);
        this.domainAxis = domainAxis;
        this.domainAxis.setPlot(this);
        this.domainAxis.addChangeListener(this);
        this.rangeAxis = rangeAxis;
        this.rangeAxis.setPlot(this);
        this.rangeAxis.addChangeListener(this);
        this.paint = Color.red;
        this.domainGridlinesVisible = true;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinesVisible = true;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
    }

    public String getPlotType() {
        return localizationResources.getString("Fast_Scatter_Plot");
    }

    public float[][] getData() {
        return this.data;
    }

    public void setData(float[][] data) {
        this.data = data;
        this.fireChangeEvent();
    }

    public PlotOrientation getOrientation() {
        return PlotOrientation.VERTICAL;
    }

    public ValueAxis getDomainAxis() {
        return this.domainAxis;
    }

    public void setDomainAxis(ValueAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.domainAxis = axis;
        this.fireChangeEvent();
    }

    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    public void setRangeAxis(ValueAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.rangeAxis = axis;
        this.fireChangeEvent();
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        this.fireChangeEvent();
    }

    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            this.fireChangeEvent();
        }
    }

    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    public void setDomainGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainGridlineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    public void setDomainGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainGridlinePaint = paint;
        this.fireChangeEvent();
    }

    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            this.fireChangeEvent();
        }
    }

    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    public void setRangeGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeGridlineStroke = stroke;
        this.fireChangeEvent();
    }

    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    public void setRangeGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeGridlinePaint = paint;
        this.fireChangeEvent();
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        if (info != null) {
            info.setPlotArea(area);
        }
        RectangleInsets insets = this.getInsets();
        insets.trim(area);
        AxisSpace space = new AxisSpace();
        space = this.domainAxis.reserveSpace(g2, this, area, RectangleEdge.BOTTOM, space);
        space = this.rangeAxis.reserveSpace(g2, this, area, RectangleEdge.LEFT, space);
        Rectangle2D dataArea = space.shrink(area, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }
        this.drawBackground(g2, dataArea);
        AxisState domainAxisState = this.domainAxis.draw(g2, dataArea.getMaxY(), area, dataArea, RectangleEdge.BOTTOM, info);
        AxisState rangeAxisState = this.rangeAxis.draw(g2, dataArea.getMinX(), area, dataArea, RectangleEdge.LEFT, info);
        this.drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
        this.drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();
        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        this.render(g2, dataArea, info, null);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        this.drawOutline(g2, dataArea);
    }

    public void render(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, CrosshairState crosshairState) {
        g2.setPaint(this.paint);
        if (this.data != null) {
            for (int i = 0; i < this.data[0].length; ++i) {
                float x = this.data[0][i];
                float y = this.data[1][i];
                int transX = (int)this.domainAxis.valueToJava2D(x, dataArea, RectangleEdge.BOTTOM);
                int transY = (int)this.rangeAxis.valueToJava2D(y, dataArea, RectangleEdge.LEFT);
                g2.fillRect(transX, transY, 1, 1);
            }
        }
    }

    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (this.isDomainGridlinesVisible()) {
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick)iterator.next();
                double v = this.domainAxis.valueToJava2D(tick.getValue(), dataArea, RectangleEdge.BOTTOM);
                Line2D.Double line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                g2.setPaint(this.getDomainGridlinePaint());
                g2.setStroke(this.getDomainGridlineStroke());
                g2.draw(line);
            }
        }
    }

    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (this.isRangeGridlinesVisible()) {
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick)iterator.next();
                double v = this.rangeAxis.valueToJava2D(tick.getValue(), dataArea, RectangleEdge.LEFT);
                Line2D.Double line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                g2.setPaint(this.getRangeGridlinePaint());
                g2.setStroke(this.getRangeGridlineStroke());
                g2.draw(line);
            }
        }
    }

    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        if (axis == this.domainAxis) {
            result = this.xDataRange;
        } else if (axis == this.rangeAxis) {
            result = this.yDataRange;
        }
        return result;
    }

    private Range calculateXDataRange(float[][] data) {
        Range result = null;
        if (data != null) {
            float lowest = Float.POSITIVE_INFINITY;
            float highest = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < data[0].length; ++i) {
                float v = data[0][i];
                if (v < lowest) {
                    lowest = v;
                }
                if (!(v > highest)) continue;
                highest = v;
            }
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }
        return result;
    }

    private Range calculateYDataRange(float[][] data) {
        Range result = null;
        if (data != null) {
            float lowest = Float.POSITIVE_INFINITY;
            float highest = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < data[0].length; ++i) {
                float v = data[1][i];
                if (v < lowest) {
                    lowest = v;
                }
                if (!(v > highest)) continue;
                highest = v;
            }
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }
        return result;
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        this.domainAxis.resizeRange(factor);
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        if (useAnchor) {
            double sourceX = source.getX();
            double anchorX = this.domainAxis.java2DToValue(sourceX, info.getDataArea(), RectangleEdge.BOTTOM);
            this.domainAxis.resizeRange2(factor, anchorX);
        } else {
            this.domainAxis.resizeRange(factor);
        }
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        this.domainAxis.zoomRange(lowerPercent, upperPercent);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.resizeRange(factor);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        if (useAnchor) {
            double sourceY = source.getY();
            double anchorY = this.rangeAxis.java2DToValue(sourceY, info.getDataArea(), RectangleEdge.LEFT);
            this.rangeAxis.resizeRange2(factor, anchorY);
        } else {
            this.rangeAxis.resizeRange(factor);
        }
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.zoomRange(lowerPercent, upperPercent);
    }

    public boolean isDomainZoomable() {
        return true;
    }

    public boolean isRangeZoomable() {
        return true;
    }

    public boolean isDomainPannable() {
        return this.domainPannable;
    }

    public void setDomainPannable(boolean pannable) {
        this.domainPannable = pannable;
    }

    public boolean isRangePannable() {
        return this.rangePannable;
    }

    public void setRangePannable(boolean pannable) {
        this.rangePannable = pannable;
    }

    public void panDomainAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (!this.isDomainPannable() || this.domainAxis == null) {
            return;
        }
        double length = this.domainAxis.getRange().getLength();
        double adj = -percent * length;
        if (this.domainAxis.isInverted()) {
            adj = -adj;
        }
        this.domainAxis.setRange(this.domainAxis.getLowerBound() + adj, this.domainAxis.getUpperBound() + adj);
    }

    public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (!this.isRangePannable() || this.rangeAxis == null) {
            return;
        }
        double length = this.rangeAxis.getRange().getLength();
        double adj = percent * length;
        if (this.rangeAxis.isInverted()) {
            adj = -adj;
        }
        this.rangeAxis.setRange(this.rangeAxis.getLowerBound() + adj, this.rangeAxis.getUpperBound() + adj);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof FastScatterPlot)) {
            return false;
        }
        FastScatterPlot that = (FastScatterPlot)obj;
        if (this.domainPannable != that.domainPannable) {
            return false;
        }
        if (this.rangePannable != that.rangePannable) {
            return false;
        }
        if (!ArrayUtilities.equal(this.data, that.data)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainAxis, that.domainAxis)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeAxis, that.rangeAxis)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke)) {
            return false;
        }
        if (!this.rangeGridlinesVisible == that.rangeGridlinesVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint)) {
            return false;
        }
        return ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke);
    }

    public Object clone() throws CloneNotSupportedException {
        FastScatterPlot clone = (FastScatterPlot)super.clone();
        if (this.data != null) {
            clone.data = ArrayUtilities.clone(this.data);
        }
        if (this.domainAxis != null) {
            clone.domainAxis = (ValueAxis)this.domainAxis.clone();
            clone.domainAxis.setPlot(clone);
            clone.domainAxis.addChangeListener(clone);
        }
        if (this.rangeAxis != null) {
            clone.rangeAxis = (ValueAxis)this.rangeAxis.clone();
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        if (this.domainAxis != null) {
            this.domainAxis.addChangeListener(this);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }
}

