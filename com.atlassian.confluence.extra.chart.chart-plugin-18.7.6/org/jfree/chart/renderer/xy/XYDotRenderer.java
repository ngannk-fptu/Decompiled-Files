/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class XYDotRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
PublicCloneable {
    private static final long serialVersionUID = -2764344339073566425L;
    private int dotWidth = 1;
    private int dotHeight = 1;
    private transient Shape legendShape = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);

    public int getDotWidth() {
        return this.dotWidth;
    }

    public void setDotWidth(int w) {
        if (w < 1) {
            throw new IllegalArgumentException("Requires w > 0.");
        }
        this.dotWidth = w;
        this.fireChangeEvent();
    }

    public int getDotHeight() {
        return this.dotHeight;
    }

    public void setDotHeight(int h) {
        if (h < 1) {
            throw new IllegalArgumentException("Requires h > 0.");
        }
        this.dotHeight = h;
        this.fireChangeEvent();
    }

    public Shape getLegendShape() {
        return this.legendShape;
    }

    public void setLegendShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendShape = shape;
        this.fireChangeEvent();
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double adjx = (double)(this.dotWidth - 1) / 2.0;
        double adjy = (double)(this.dotHeight - 1) / 2.0;
        if (!Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, xAxisLocation) - adjx;
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation) - adjy;
            g2.setPaint(this.getItemPaint(series, item));
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.fillRect((int)transY, (int)transX, this.dotHeight, this.dotWidth);
            } else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillRect((int)transX, (int)transY, this.dotWidth, this.dotHeight);
            }
            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            this.updateCrosshairValues(crosshairState, x, y, domainAxisIndex, rangeAxisIndex, transX, transY, orientation);
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = this.getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }
        LegendItem result = null;
        if (this.getItemVisible(series, 0)) {
            String label;
            String description = label = this.getLegendItemLabelGenerator().generateLabel(dataset, series);
            String toolTipText = null;
            if (this.getLegendItemToolTipGenerator() != null) {
                toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
            }
            String urlText = null;
            if (this.getLegendItemURLGenerator() != null) {
                urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
            }
            Paint fillPaint = this.lookupSeriesPaint(series);
            result = new LegendItem(label, description, toolTipText, urlText, this.getLegendShape(), fillPaint);
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

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDotRenderer)) {
            return false;
        }
        XYDotRenderer that = (XYDotRenderer)obj;
        if (this.dotWidth != that.dotWidth) {
            return false;
        }
        if (this.dotHeight != that.dotHeight) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendShape, that.legendShape)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendShape = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendShape, stream);
    }
}

