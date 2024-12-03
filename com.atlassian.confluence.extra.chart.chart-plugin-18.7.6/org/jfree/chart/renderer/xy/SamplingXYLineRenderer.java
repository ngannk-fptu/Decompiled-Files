/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class SamplingXYLineRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
Cloneable,
PublicCloneable,
Serializable {
    private transient Shape legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);

    public Shape getLegendLine() {
        return this.legendLine;
    }

    public void setLegendLine(Shape line) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");
        }
        this.legendLine = line;
        this.fireChangeEvent();
    }

    public int getPassCount() {
        return 1;
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        double dpi = 72.0;
        State state = new State(info);
        state.seriesPath = new GeneralPath();
        state.intervalPath = new GeneralPath();
        state.dX = 72.0 / dpi;
        return state;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        State s = (State)state;
        if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
            float x = (float)transX1;
            float y = (float)transY1;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                x = (float)transY1;
                y = (float)transX1;
            }
            if (s.lastPointGood) {
                if (Math.abs((double)x - s.lastX) > s.dX) {
                    s.seriesPath.lineTo(x, y);
                    if (s.lowY < s.highY) {
                        s.intervalPath.moveTo((float)s.lastX, (float)s.lowY);
                        s.intervalPath.lineTo((float)s.lastX, (float)s.highY);
                    }
                    s.lastX = x;
                    s.openY = y;
                    s.highY = y;
                    s.lowY = y;
                    s.closeY = y;
                } else {
                    s.highY = Math.max(s.highY, (double)y);
                    s.lowY = Math.min(s.lowY, (double)y);
                    s.closeY = y;
                }
            } else {
                s.seriesPath.moveTo(x, y);
                s.lastX = x;
                s.openY = y;
                s.highY = y;
                s.lowY = y;
                s.closeY = y;
            }
            s.lastPointGood = true;
        } else {
            s.lastPointGood = false;
        }
        if (item == s.getLastItemIndex()) {
            PathIterator pi = s.seriesPath.getPathIterator(null);
            int count = 0;
            while (!pi.isDone()) {
                ++count;
                pi.next();
            }
            g2.setStroke(this.getItemStroke(series, item));
            g2.setPaint(this.getItemPaint(series, item));
            g2.draw(s.seriesPath);
            g2.draw(s.intervalPath);
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = this.getPlot();
        if (plot == null) {
            return null;
        }
        LegendItem result = null;
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset != null && this.getItemVisible(series, 0)) {
            String label = this.getLegendItemLabelGenerator().generateLabel(dataset, series);
            result = new LegendItem(label);
            result.setLabelFont(this.lookupLegendTextFont(series));
            Paint labelPaint = this.lookupLegendTextPaint(series);
            if (labelPaint != null) {
                result.setLabelPaint(labelPaint);
            }
            result.setSeriesKey(dataset.getSeriesKey(series));
            result.setSeriesIndex(series);
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        SamplingXYLineRenderer clone = (SamplingXYLineRenderer)super.clone();
        if (this.legendLine != null) {
            clone.legendLine = ShapeUtilities.clone(this.legendLine);
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SamplingXYLineRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        SamplingXYLineRenderer that = (SamplingXYLineRenderer)obj;
        return ShapeUtilities.equal(this.legendLine, that.legendLine);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendLine = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendLine, stream);
    }

    public static class State
    extends XYItemRendererState {
        GeneralPath seriesPath;
        GeneralPath intervalPath;
        double dX = 1.0;
        double lastX;
        double openY = 0.0;
        double highY = 0.0;
        double lowY = 0.0;
        double closeY = 0.0;
        boolean lastPointGood;

        public State(PlotRenderingInfo info) {
            super(info);
        }

        public void startSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
            this.seriesPath.reset();
            this.lastPointGood = false;
            super.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
        }
    }
}

