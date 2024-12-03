/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class XYErrorRenderer
extends XYLineAndShapeRenderer {
    static final long serialVersionUID = 5162283570955172424L;
    private boolean drawXError = true;
    private boolean drawYError = true;
    private double capLength = 4.0;
    private transient Paint errorPaint = null;
    private transient Stroke errorStroke = null;

    public XYErrorRenderer() {
        super(false, true);
    }

    public boolean getDrawXError() {
        return this.drawXError;
    }

    public void setDrawXError(boolean draw) {
        if (this.drawXError != draw) {
            this.drawXError = draw;
            this.fireChangeEvent();
        }
    }

    public boolean getDrawYError() {
        return this.drawYError;
    }

    public void setDrawYError(boolean draw) {
        if (this.drawYError != draw) {
            this.drawYError = draw;
            this.fireChangeEvent();
        }
    }

    public double getCapLength() {
        return this.capLength;
    }

    public void setCapLength(double length) {
        this.capLength = length;
        this.fireChangeEvent();
    }

    public Paint getErrorPaint() {
        return this.errorPaint;
    }

    public void setErrorPaint(Paint paint) {
        this.errorPaint = paint;
        this.fireChangeEvent();
    }

    public Stroke getErrorStroke() {
        return this.errorStroke;
    }

    public void setErrorStroke(Stroke stroke) {
        this.errorStroke = stroke;
        this.fireChangeEvent();
    }

    public Range findDomainBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findDomainBounds(dataset, true);
        }
        return null;
    }

    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findRangeBounds(dataset, true);
        }
        return null;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (pass == 0 && dataset instanceof IntervalXYDataset && this.getItemVisible(series, item)) {
            Line2D.Double line;
            double adj;
            Line2D.Double cap2;
            Line2D.Double cap1;
            RectangleEdge edge;
            IntervalXYDataset ixyd = (IntervalXYDataset)dataset;
            PlotOrientation orientation = plot.getOrientation();
            if (this.drawXError) {
                double x0 = ixyd.getStartXValue(series, item);
                double x1 = ixyd.getEndXValue(series, item);
                double y = ixyd.getYValue(series, item);
                edge = plot.getDomainAxisEdge();
                double xx0 = domainAxis.valueToJava2D(x0, dataArea, edge);
                double xx1 = domainAxis.valueToJava2D(x1, dataArea, edge);
                double yy = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
                cap1 = null;
                cap2 = null;
                adj = this.capLength / 2.0;
                if (orientation == PlotOrientation.VERTICAL) {
                    line = new Line2D.Double(xx0, yy, xx1, yy);
                    cap1 = new Line2D.Double(xx0, yy - adj, xx0, yy + adj);
                    cap2 = new Line2D.Double(xx1, yy - adj, xx1, yy + adj);
                } else {
                    line = new Line2D.Double(yy, xx0, yy, xx1);
                    cap1 = new Line2D.Double(yy - adj, xx0, yy + adj, xx0);
                    cap2 = new Line2D.Double(yy - adj, xx1, yy + adj, xx1);
                }
                if (this.errorPaint != null) {
                    g2.setPaint(this.errorPaint);
                } else {
                    g2.setPaint(this.getItemPaint(series, item));
                }
                if (this.errorStroke != null) {
                    g2.setStroke(this.errorStroke);
                } else {
                    g2.setStroke(this.getItemStroke(series, item));
                }
                g2.draw(line);
                g2.draw(cap1);
                g2.draw(cap2);
            }
            if (this.drawYError) {
                double y0 = ixyd.getStartYValue(series, item);
                double y1 = ixyd.getEndYValue(series, item);
                double x = ixyd.getXValue(series, item);
                edge = plot.getRangeAxisEdge();
                double yy0 = rangeAxis.valueToJava2D(y0, dataArea, edge);
                double yy1 = rangeAxis.valueToJava2D(y1, dataArea, edge);
                double xx = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
                cap1 = null;
                cap2 = null;
                adj = this.capLength / 2.0;
                if (orientation == PlotOrientation.VERTICAL) {
                    line = new Line2D.Double(xx, yy0, xx, yy1);
                    cap1 = new Line2D.Double(xx - adj, yy0, xx + adj, yy0);
                    cap2 = new Line2D.Double(xx - adj, yy1, xx + adj, yy1);
                } else {
                    line = new Line2D.Double(yy0, xx, yy1, xx);
                    cap1 = new Line2D.Double(yy0, xx - adj, yy0, xx + adj);
                    cap2 = new Line2D.Double(yy1, xx - adj, yy1, xx + adj);
                }
                if (this.errorPaint != null) {
                    g2.setPaint(this.errorPaint);
                } else {
                    g2.setPaint(this.getItemPaint(series, item));
                }
                if (this.errorStroke != null) {
                    g2.setStroke(this.errorStroke);
                } else {
                    g2.setStroke(this.getItemStroke(series, item));
                }
                g2.draw(line);
                g2.draw(cap1);
                g2.draw(cap2);
            }
        }
        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYErrorRenderer)) {
            return false;
        }
        XYErrorRenderer that = (XYErrorRenderer)obj;
        if (this.drawXError != that.drawXError) {
            return false;
        }
        if (this.drawYError != that.drawYError) {
            return false;
        }
        if (this.capLength != that.capLength) {
            return false;
        }
        if (!PaintUtilities.equal(this.errorPaint, that.errorPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.errorStroke, that.errorStroke)) {
            return false;
        }
        return super.equals(obj);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.errorPaint = SerialUtilities.readPaint(stream);
        this.errorStroke = SerialUtilities.readStroke(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.errorPaint, stream);
        SerialUtilities.writeStroke(this.errorStroke, stream);
    }
}

