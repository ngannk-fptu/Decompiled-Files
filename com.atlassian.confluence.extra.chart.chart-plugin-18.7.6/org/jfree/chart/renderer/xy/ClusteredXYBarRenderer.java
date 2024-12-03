/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class ClusteredXYBarRenderer
extends XYBarRenderer
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 5864462149177133147L;
    private boolean centerBarAtStartValue;

    public ClusteredXYBarRenderer() {
        this(0.0, false);
    }

    public ClusteredXYBarRenderer(double margin, boolean centerBarAtStartValue) {
        super(margin);
        this.centerBarAtStartValue = centerBarAtStartValue;
    }

    public int getPassCount() {
        return 2;
    }

    public Range findDomainBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        if (this.centerBarAtStartValue) {
            return this.findDomainBoundsWithOffset((IntervalXYDataset)dataset);
        }
        return super.findDomainBounds(dataset);
    }

    protected Range findDomainBoundsWithOffset(IntervalXYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; ++series) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; ++item) {
                double lvalue = dataset.getStartXValue(series, item);
                double uvalue = dataset.getEndXValue(series, item);
                double offset = (uvalue - lvalue) / 2.0;
                minimum = Math.min(minimum, lvalue -= offset);
                maximum = Math.max(maximum, uvalue -= offset);
            }
        }
        if (minimum > maximum) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        double m;
        double y1;
        double y0;
        IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
        if (this.getUseYInterval()) {
            y0 = intervalDataset.getStartYValue(series, item);
            y1 = intervalDataset.getEndYValue(series, item);
        } else {
            y0 = this.getBase();
            y1 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(y0) || Double.isNaN(y1)) {
            return;
        }
        double yy0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double x0 = intervalDataset.getStartXValue(series, item);
        double xx0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
        double x1 = intervalDataset.getEndXValue(series, item);
        double xx1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double intervalW = xx1 - xx0;
        double baseX = xx0;
        if (this.centerBarAtStartValue) {
            baseX -= intervalW / 2.0;
        }
        if ((m = this.getMargin()) > 0.0) {
            double cut = intervalW * this.getMargin();
            intervalW -= cut;
            baseX += cut / 2.0;
        }
        double intervalH = Math.abs(yy0 - yy1);
        PlotOrientation orientation = plot.getOrientation();
        int numSeries = dataset.getSeriesCount();
        double seriesBarWidth = intervalW / (double)numSeries;
        Rectangle2D.Double bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            double barY0 = baseX + seriesBarWidth * (double)series;
            double barY1 = barY0 + seriesBarWidth;
            double rx = Math.min(yy0, yy1);
            double rw = intervalH;
            double ry = Math.min(barY0, barY1);
            double rh = Math.abs(barY1 - barY0);
            bar = new Rectangle2D.Double(rx, ry, rw, rh);
        } else if (orientation == PlotOrientation.VERTICAL) {
            double barX0 = baseX + seriesBarWidth * (double)series;
            double barX1 = barX0 + seriesBarWidth;
            double rx = Math.min(barX0, barX1);
            double rw = Math.abs(barX1 - barX0);
            double ry = Math.min(yy0, yy1);
            double rh = intervalH;
            bar = new Rectangle2D.Double(rx, ry, rw, rh);
        }
        boolean positive = y1 > 0.0;
        boolean inverted = rangeAxis.isInverted();
        RectangleEdge barBase = orientation == PlotOrientation.HORIZONTAL ? (positive && inverted || !positive && !inverted ? RectangleEdge.RIGHT : RectangleEdge.LEFT) : (positive && !inverted || !positive && inverted ? RectangleEdge.BOTTOM : RectangleEdge.TOP);
        if (pass == 0 && this.getShadowsVisible()) {
            this.getBarPainter().paintBarShadow(g2, this, series, item, bar, barBase, !this.getUseYInterval());
        }
        if (pass == 1) {
            EntityCollection entities;
            this.getBarPainter().paintBar(g2, this, series, item, bar, barBase);
            if (this.isItemLabelVisible(series, item)) {
                XYItemLabelGenerator generator = this.getItemLabelGenerator(series, item);
                this.drawItemLabel(g2, dataset, series, item, plot, generator, bar, y1 < 0.0);
            }
            if (info != null && (entities = info.getOwner().getEntityCollection()) != null) {
                this.addEntity(entities, bar, dataset, series, item, bar.getCenterX(), bar.getCenterY());
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClusteredXYBarRenderer)) {
            return false;
        }
        ClusteredXYBarRenderer that = (ClusteredXYBarRenderer)obj;
        if (this.centerBarAtStartValue != that.centerBarAtStartValue) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

