/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.PaintUtilities;

public class WaterfallBarRenderer
extends BarRenderer {
    private static final long serialVersionUID = -2482910643727230911L;
    private transient Paint firstBarPaint;
    private transient Paint lastBarPaint;
    private transient Paint positiveBarPaint;
    private transient Paint negativeBarPaint;

    public WaterfallBarRenderer() {
        this(new GradientPaint(0.0f, 0.0f, new Color(34, 34, 255), 0.0f, 0.0f, new Color(102, 102, 255)), new GradientPaint(0.0f, 0.0f, new Color(34, 255, 34), 0.0f, 0.0f, new Color(102, 255, 102)), new GradientPaint(0.0f, 0.0f, new Color(255, 34, 34), 0.0f, 0.0f, new Color(255, 102, 102)), new GradientPaint(0.0f, 0.0f, new Color(255, 255, 34), 0.0f, 0.0f, new Color(255, 255, 102)));
    }

    public WaterfallBarRenderer(Paint firstBarPaint, Paint positiveBarPaint, Paint negativeBarPaint, Paint lastBarPaint) {
        if (firstBarPaint == null) {
            throw new IllegalArgumentException("Null 'firstBarPaint' argument");
        }
        if (positiveBarPaint == null) {
            throw new IllegalArgumentException("Null 'positiveBarPaint' argument");
        }
        if (negativeBarPaint == null) {
            throw new IllegalArgumentException("Null 'negativeBarPaint' argument");
        }
        if (lastBarPaint == null) {
            throw new IllegalArgumentException("Null 'lastBarPaint' argument");
        }
        this.firstBarPaint = firstBarPaint;
        this.lastBarPaint = lastBarPaint;
        this.positiveBarPaint = positiveBarPaint;
        this.negativeBarPaint = negativeBarPaint;
        this.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_VERTICAL));
        this.setMinimumBarLength(1.0);
    }

    public Paint getFirstBarPaint() {
        return this.firstBarPaint;
    }

    public void setFirstBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.firstBarPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getLastBarPaint() {
        return this.lastBarPaint;
    }

    public void setLastBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.lastBarPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getPositiveBarPaint() {
        return this.positiveBarPaint;
    }

    public void setPositiveBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.positiveBarPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getNegativeBarPaint() {
        return this.negativeBarPaint;
    }

    public void setNegativeBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.negativeBarPaint = paint;
        this.fireChangeEvent();
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        boolean allItemsNull = true;
        double minimum = 0.0;
        double maximum = 0.0;
        int columnCount = dataset.getColumnCount();
        for (int row = 0; row < dataset.getRowCount(); ++row) {
            double runningTotal = 0.0;
            for (int column = 0; column <= columnCount - 1; ++column) {
                Number n = dataset.getValue(row, column);
                if (n == null) continue;
                allItemsNull = false;
                double value = n.doubleValue();
                runningTotal = column == columnCount - 1 ? value : (runningTotal += value);
                minimum = Math.min(minimum, runningTotal);
                maximum = Math.max(maximum, runningTotal);
            }
        }
        if (!allItemsNull) {
            return new Range(minimum, maximum);
        }
        return null;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        EntityCollection entities;
        CategoryItemLabelGenerator generator;
        double previous = state.getSeriesRunningTotal();
        if (column == dataset.getColumnCount() - 1) {
            previous = 0.0;
        }
        double current = 0.0;
        Number n = dataset.getValue(row, column);
        if (n != null) {
            current = previous + n.doubleValue();
        }
        state.setSeriesRunningTotal(current);
        int categoryCount = this.getColumnCount();
        PlotOrientation orientation = plot.getOrientation();
        double rectX = 0.0;
        double rectY = 0.0;
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        double j2dy0 = rangeAxis.valueToJava2D(previous, dataArea, rangeAxisLocation);
        double j2dy1 = rangeAxis.valueToJava2D(current, dataArea, rangeAxisLocation);
        double valDiff = current - previous;
        if (j2dy1 < j2dy0) {
            double temp = j2dy1;
            j2dy1 = j2dy0;
            j2dy0 = temp;
        }
        double rectWidth = state.getBarWidth();
        double rectHeight = Math.max(this.getMinimumBarLength(), Math.abs(j2dy1 - j2dy0));
        Comparable seriesKey = dataset.getRowKey(row);
        Comparable categoryKey = dataset.getColumnKey(column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            rectY = domainAxis.getCategorySeriesMiddle(categoryKey, seriesKey, dataset, this.getItemMargin(), dataArea, RectangleEdge.LEFT);
            rectX = j2dy0;
            rectHeight = state.getBarWidth();
            rectY -= rectHeight / 2.0;
            rectWidth = Math.max(this.getMinimumBarLength(), Math.abs(j2dy1 - j2dy0));
        } else if (orientation == PlotOrientation.VERTICAL) {
            rectX = domainAxis.getCategorySeriesMiddle(categoryKey, seriesKey, dataset, this.getItemMargin(), dataArea, RectangleEdge.TOP);
            rectX -= rectWidth / 2.0;
            rectY = j2dy0;
        }
        Rectangle2D.Double bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = this.getFirstBarPaint();
        seriesPaint = column == 0 ? this.getFirstBarPaint() : (column == categoryCount - 1 ? this.getLastBarPaint() : (valDiff < 0.0 ? this.getNegativeBarPaint() : (valDiff > 0.0 ? this.getPositiveBarPaint() : this.getLastBarPaint())));
        if (this.getGradientPaintTransformer() != null && seriesPaint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint)seriesPaint;
            seriesPaint = this.getGradientPaintTransformer().transform(gp, bar);
        }
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (this.isDrawBarOutline() && state.getBarWidth() > 3.0) {
            Stroke stroke = this.getItemOutlineStroke(row, column);
            Paint paint = this.getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
        if ((generator = this.getItemLabelGenerator(row, column)) != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, valDiff < 0.0);
        }
        if ((entities = state.getEntityCollection()) != null) {
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof WaterfallBarRenderer)) {
            return false;
        }
        WaterfallBarRenderer that = (WaterfallBarRenderer)obj;
        if (!PaintUtilities.equal(this.firstBarPaint, that.firstBarPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.lastBarPaint, that.lastBarPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.positiveBarPaint, that.positiveBarPaint)) {
            return false;
        }
        return PaintUtilities.equal(this.negativeBarPaint, that.negativeBarPaint);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.firstBarPaint, stream);
        SerialUtilities.writePaint(this.lastBarPaint, stream);
        SerialUtilities.writePaint(this.positiveBarPaint, stream);
        SerialUtilities.writePaint(this.negativeBarPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.firstBarPaint = SerialUtilities.readPaint(stream);
        this.lastBarPaint = SerialUtilities.readPaint(stream);
        this.positiveBarPaint = SerialUtilities.readPaint(stream);
        this.negativeBarPaint = SerialUtilities.readPaint(stream);
    }
}

