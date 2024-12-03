/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;

public class DefaultPolarItemRenderer
extends AbstractRenderer
implements PolarItemRenderer {
    private PolarPlot plot;
    private BooleanList seriesFilled = new BooleanList();

    public void setPlot(PolarPlot plot) {
        this.plot = plot;
    }

    public PolarPlot getPlot() {
        return this.plot;
    }

    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        PolarPlot p = this.getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }

    public boolean isSeriesFilled(int series) {
        boolean result = false;
        Boolean b = this.seriesFilled.getBoolean(series);
        if (b != null) {
            result = b;
        }
        return result;
    }

    public void setSeriesFilled(int series, boolean filled) {
        this.seriesFilled.setBoolean(series, BooleanUtilities.valueOf(filled));
    }

    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {
        Polygon poly = new Polygon();
        int numPoints = dataset.getItemCount(seriesIndex);
        for (int i = 0; i < numPoints; ++i) {
            double theta = dataset.getXValue(seriesIndex, i);
            double radius = dataset.getYValue(seriesIndex, i);
            Point p = plot.translateValueThetaRadiusToJava2D(theta, radius, dataArea);
            poly.addPoint(p.x, p.y);
        }
        g2.setPaint(this.lookupSeriesPaint(seriesIndex));
        g2.setStroke(this.lookupSeriesStroke(seriesIndex));
        if (this.isSeriesFilled(seriesIndex)) {
            Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, 0.5f));
            g2.fill(poly);
            g2.setComposite(savedComposite);
        } else {
            g2.draw(poly);
        }
    }

    public void drawAngularGridLines(Graphics2D g2, PolarPlot plot, List ticks, Rectangle2D dataArea) {
        g2.setFont(plot.getAngleLabelFont());
        g2.setStroke(plot.getAngleGridlineStroke());
        g2.setPaint(plot.getAngleGridlinePaint());
        double axisMin = plot.getAxis().getLowerBound();
        double maxRadius = plot.getMaxRadius();
        Point center = plot.translateValueThetaRadiusToJava2D(axisMin, axisMin, dataArea);
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            NumberTick tick = (NumberTick)iterator.next();
            Point p = plot.translateValueThetaRadiusToJava2D(tick.getNumber().doubleValue(), maxRadius, dataArea);
            g2.setPaint(plot.getAngleGridlinePaint());
            g2.drawLine(center.x, center.y, p.x, p.y);
            if (!plot.isAngleLabelsVisible()) continue;
            int x = p.x;
            int y = p.y;
            g2.setPaint(plot.getAngleLabelPaint());
            TextUtilities.drawAlignedString(tick.getText(), g2, x, y, TextAnchor.CENTER);
        }
    }

    public void drawRadialGridLines(Graphics2D g2, PolarPlot plot, ValueAxis radialAxis, List ticks, Rectangle2D dataArea) {
        g2.setFont(radialAxis.getTickLabelFont());
        g2.setPaint(plot.getRadiusGridlinePaint());
        g2.setStroke(plot.getRadiusGridlineStroke());
        double axisMin = radialAxis.getLowerBound();
        Point center = plot.translateValueThetaRadiusToJava2D(axisMin, axisMin, dataArea);
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            NumberTick tick = (NumberTick)iterator.next();
            Point p = plot.translateValueThetaRadiusToJava2D(90.0, tick.getNumber().doubleValue(), dataArea);
            int r = p.x - center.x;
            int upperLeftX = center.x - r;
            int upperLeftY = center.y - r;
            int d = 2 * r;
            Ellipse2D.Double ring = new Ellipse2D.Double(upperLeftX, upperLeftY, d, d);
            g2.setPaint(plot.getRadiusGridlinePaint());
            g2.draw(ring);
        }
    }

    public LegendItem getLegendItem(int series) {
        XYDataset dataset;
        LegendItem result = null;
        PolarPlot polarPlot = this.getPlot();
        if (polarPlot != null && (dataset = polarPlot.getDataset()) != null) {
            String label;
            String description = label = dataset.getSeriesKey(series).toString();
            Shape shape = this.lookupSeriesShape(series);
            Paint paint = this.lookupSeriesPaint(series);
            Paint outlinePaint = this.lookupSeriesOutlinePaint(series);
            Stroke outlineStroke = this.lookupSeriesOutlineStroke(series);
            result = new LegendItem(label, description, null, null, shape, paint, outlineStroke, outlinePaint);
            result.setDataset(dataset);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultPolarItemRenderer)) {
            return false;
        }
        DefaultPolarItemRenderer that = (DefaultPolarItemRenderer)obj;
        if (!this.seriesFilled.equals(that.seriesFilled)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultPolarItemRenderer clone = (DefaultPolarItemRenderer)super.clone();
        clone.seriesFilled = (BooleanList)this.seriesFilled.clone();
        return clone;
    }
}

