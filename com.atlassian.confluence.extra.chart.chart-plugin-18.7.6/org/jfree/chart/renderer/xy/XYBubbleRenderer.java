/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class XYBubbleRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
PublicCloneable {
    public static final long serialVersionUID = -5221991598674249125L;
    public static final int SCALE_ON_BOTH_AXES = 0;
    public static final int SCALE_ON_DOMAIN_AXIS = 1;
    public static final int SCALE_ON_RANGE_AXIS = 2;
    private int scaleType;

    public XYBubbleRenderer() {
        this(0);
    }

    public XYBubbleRenderer(int scaleType) {
        if (scaleType < 0 || scaleType > 2) {
            throw new IllegalArgumentException("Invalid 'scaleType'.");
        }
        this.scaleType = scaleType;
        this.setBaseLegendShape(new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));
    }

    public int getScaleType() {
        return this.scaleType;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = Double.NaN;
        if (dataset instanceof XYZDataset) {
            XYZDataset xyzData = (XYZDataset)dataset;
            z = xyzData.getZValue(series, item);
        }
        if (!Double.isNaN(z)) {
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, domainAxisLocation);
            double transY = rangeAxis.valueToJava2D(y, dataArea, rangeAxisLocation);
            double transDomain = 0.0;
            double transRange = 0.0;
            switch (this.getScaleType()) {
                case 1: {
                    double zero = domainAxis.valueToJava2D(0.0, dataArea, domainAxisLocation);
                    transRange = transDomain = domainAxis.valueToJava2D(z, dataArea, domainAxisLocation) - zero;
                    break;
                }
                case 2: {
                    double zero = rangeAxis.valueToJava2D(0.0, dataArea, rangeAxisLocation);
                    transDomain = transRange = zero - rangeAxis.valueToJava2D(z, dataArea, rangeAxisLocation);
                    break;
                }
                default: {
                    double zero1 = domainAxis.valueToJava2D(0.0, dataArea, domainAxisLocation);
                    double zero2 = rangeAxis.valueToJava2D(0.0, dataArea, rangeAxisLocation);
                    transDomain = domainAxis.valueToJava2D(z, dataArea, domainAxisLocation) - zero1;
                    transRange = zero2 - rangeAxis.valueToJava2D(z, dataArea, rangeAxisLocation);
                }
            }
            transDomain = Math.abs(transDomain);
            transRange = Math.abs(transRange);
            Ellipse2D.Double circle = null;
            if (orientation == PlotOrientation.VERTICAL) {
                circle = new Ellipse2D.Double(transX - transDomain / 2.0, transY - transRange / 2.0, transDomain, transRange);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                circle = new Ellipse2D.Double(transY - transRange / 2.0, transX - transDomain / 2.0, transRange, transDomain);
            }
            g2.setPaint(this.getItemPaint(series, item));
            g2.fill(circle);
            g2.setStroke(this.getItemOutlineStroke(series, item));
            g2.setPaint(this.getItemOutlinePaint(series, item));
            g2.draw(circle);
            if (this.isItemLabelVisible(series, item)) {
                if (orientation == PlotOrientation.VERTICAL) {
                    this.drawItemLabel(g2, orientation, dataset, series, item, transX, transY, false);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    this.drawItemLabel(g2, orientation, dataset, series, item, transY, transX, false);
                }
            }
            EntityCollection entities = null;
            if (info != null && (entities = info.getOwner().getEntityCollection()) != null && circle.intersects(dataArea)) {
                this.addEntity(entities, circle, dataset, series, item, circle.getCenterX(), circle.getCenterY());
            }
            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            this.updateCrosshairValues(crosshairState, x, y, domainAxisIndex, rangeAxisIndex, transX, transY, orientation);
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot plot = this.getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset != null && this.getItemVisible(series, 0)) {
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
            Shape shape = this.lookupLegendShape(series);
            Paint paint = this.lookupSeriesPaint(series);
            Paint outlinePaint = this.lookupSeriesOutlinePaint(series);
            Stroke outlineStroke = this.lookupSeriesOutlineStroke(series);
            result = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
            result.setLabelFont(this.lookupLegendTextFont(series));
            Paint labelPaint = this.lookupLegendTextPaint(series);
            if (labelPaint != null) {
                result.setLabelPaint(labelPaint);
            }
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
            result.setSeriesKey(dataset.getSeriesKey(series));
            result.setSeriesIndex(series);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBubbleRenderer)) {
            return false;
        }
        XYBubbleRenderer that = (XYBubbleRenderer)obj;
        if (this.scaleType != that.scaleType) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

