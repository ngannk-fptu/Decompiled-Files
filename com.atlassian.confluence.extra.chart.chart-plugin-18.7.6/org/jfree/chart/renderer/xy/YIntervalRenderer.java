/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.xy;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class YIntervalRenderer
extends AbstractXYItemRenderer
implements XYItemRenderer,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -2951586537224143260L;
    private XYItemLabelGenerator additionalItemLabelGenerator = null;

    public XYItemLabelGenerator getAdditionalItemLabelGenerator() {
        return this.additionalItemLabelGenerator;
    }

    public void setAdditionalItemLabelGenerator(XYItemLabelGenerator generator) {
        this.additionalItemLabelGenerator = generator;
        this.fireChangeEvent();
    }

    public Range findRangeBounds(XYDataset dataset) {
        return this.findRangeBounds(dataset, true);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
        double x = intervalDataset.getXValue(series, item);
        double yLow = intervalDataset.getStartYValue(series, item);
        double yHigh = intervalDataset.getEndYValue(series, item);
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double xx = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
        double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, yAxisLocation);
        double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, yAxisLocation);
        Paint p = this.getItemPaint(series, item);
        Stroke s = this.getItemStroke(series, item);
        Line2D.Double line = null;
        Shape shape = this.getItemShape(series, item);
        Shape top = null;
        Shape bottom = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(yyLow, xx, yyHigh, xx);
            top = ShapeUtilities.createTranslatedShape(shape, yyHigh, xx);
            bottom = ShapeUtilities.createTranslatedShape(shape, yyLow, xx);
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(xx, yyLow, xx, yyHigh);
            top = ShapeUtilities.createTranslatedShape(shape, xx, yyHigh);
            bottom = ShapeUtilities.createTranslatedShape(shape, xx, yyLow);
        }
        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(line);
        g2.fill(top);
        g2.fill(bottom);
        if (this.isItemLabelVisible(series, item)) {
            this.drawItemLabel(g2, orientation, dataset, series, item, xx, yyHigh, false);
            this.drawAdditionalItemLabel(g2, orientation, dataset, series, item, xx, yyLow);
        }
        if (entities != null) {
            this.addEntity(entities, line.getBounds(), dataset, series, item, 0.0, 0.0);
        }
    }

    private void drawAdditionalItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y) {
        if (this.additionalItemLabelGenerator == null) {
            return;
        }
        Font labelFont = this.getItemLabelFont(series, item);
        Paint paint = this.getItemLabelPaint(series, item);
        g2.setFont(labelFont);
        g2.setPaint(paint);
        String label = this.additionalItemLabelGenerator.generateLabel(dataset, series, item);
        ItemLabelPosition position = this.getNegativeItemLabelPosition(series, item);
        Point2D anchorPoint = this.calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
        TextUtilities.drawRotatedString(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof YIntervalRenderer)) {
            return false;
        }
        YIntervalRenderer that = (YIntervalRenderer)obj;
        if (!ObjectUtilities.equal(this.additionalItemLabelGenerator, that.additionalItemLabelGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

