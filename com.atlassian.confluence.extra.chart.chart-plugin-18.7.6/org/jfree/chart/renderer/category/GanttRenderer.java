/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;

public class GanttRenderer
extends IntervalBarRenderer
implements Serializable {
    private static final long serialVersionUID = -4010349116350119512L;
    private transient Paint completePaint;
    private transient Paint incompletePaint;
    private double startPercent;
    private double endPercent;

    public GanttRenderer() {
        this.setIncludeBaseInRange(false);
        this.completePaint = Color.green;
        this.incompletePaint = Color.red;
        this.startPercent = 0.35;
        this.endPercent = 0.65;
    }

    public Paint getCompletePaint() {
        return this.completePaint;
    }

    public void setCompletePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.completePaint = paint;
        this.fireChangeEvent();
    }

    public Paint getIncompletePaint() {
        return this.incompletePaint;
    }

    public void setIncompletePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.incompletePaint = paint;
        this.fireChangeEvent();
    }

    public double getStartPercent() {
        return this.startPercent;
    }

    public void setStartPercent(double percent) {
        this.startPercent = percent;
        this.fireChangeEvent();
    }

    public double getEndPercent() {
        return this.endPercent;
    }

    public void setEndPercent(double percent) {
        this.endPercent = percent;
        this.fireChangeEvent();
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (dataset instanceof GanttCategoryDataset) {
            GanttCategoryDataset gcd = (GanttCategoryDataset)dataset;
            this.drawTasks(g2, state, dataArea, plot, domainAxis, rangeAxis, gcd, row, column);
        } else {
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);
        }
    }

    protected void drawTasks(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, GanttCategoryDataset dataset, int row, int column) {
        int count = dataset.getSubIntervalCount(row, column);
        if (count == 0) {
            this.drawTask(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }
        PlotOrientation orientation = plot.getOrientation();
        for (int subinterval = 0; subinterval < count; ++subinterval) {
            EntityCollection entities;
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            Number value0 = dataset.getStartValue(row, column, subinterval);
            if (value0 == null) {
                return;
            }
            double translatedValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
            Number value1 = dataset.getEndValue(row, column, subinterval);
            if (value1 == null) {
                return;
            }
            double translatedValue1 = rangeAxis.valueToJava2D(value1.doubleValue(), dataArea, rangeAxisLocation);
            if (translatedValue1 < translatedValue0) {
                double temp = translatedValue1;
                translatedValue1 = translatedValue0;
                translatedValue0 = temp;
            }
            double rectStart = this.calculateBarW0(plot, plot.getOrientation(), dataArea, domainAxis, state, row, column);
            double rectLength = Math.abs(translatedValue1 - translatedValue0);
            double rectBreadth = state.getBarWidth();
            Rectangle2D.Double bar = null;
            RectangleEdge barBase = null;
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                bar = new Rectangle2D.Double(translatedValue0, rectStart, rectLength, rectBreadth);
                barBase = RectangleEdge.LEFT;
            } else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                bar = new Rectangle2D.Double(rectStart, translatedValue0, rectBreadth, rectLength);
                barBase = RectangleEdge.BOTTOM;
            }
            Rectangle2D.Double completeBar = null;
            Rectangle2D.Double incompleteBar = null;
            Number percent = dataset.getPercentComplete(row, column, subinterval);
            double start = this.getStartPercent();
            double end = this.getEndPercent();
            if (percent != null) {
                double p = percent.doubleValue();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    completeBar = new Rectangle2D.Double(translatedValue0, rectStart + start * rectBreadth, rectLength * p, rectBreadth * (end - start));
                    incompleteBar = new Rectangle2D.Double(translatedValue0 + rectLength * p, rectStart + start * rectBreadth, rectLength * (1.0 - p), rectBreadth * (end - start));
                } else if (orientation == PlotOrientation.VERTICAL) {
                    completeBar = new Rectangle2D.Double(rectStart + start * rectBreadth, translatedValue0 + rectLength * (1.0 - p), rectBreadth * (end - start), rectLength * p);
                    incompleteBar = new Rectangle2D.Double(rectStart + start * rectBreadth, translatedValue0, rectBreadth * (end - start), rectLength * (1.0 - p));
                }
            }
            if (this.getShadowsVisible()) {
                this.getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase, true);
            }
            this.getBarPainter().paintBar(g2, this, row, column, bar, barBase);
            if (completeBar != null) {
                g2.setPaint(this.getCompletePaint());
                g2.fill(completeBar);
            }
            if (incompleteBar != null) {
                g2.setPaint(this.getIncompletePaint());
                g2.fill(incompleteBar);
            }
            if (this.isDrawBarOutline() && state.getBarWidth() > 3.0) {
                g2.setStroke(this.getItemStroke(row, column));
                g2.setPaint(this.getItemOutlinePaint(row, column));
                g2.draw(bar);
            }
            if (subinterval == count - 1) {
                int datasetIndex = plot.indexOf(dataset);
                Comparable columnKey = dataset.getColumnKey(column);
                Comparable rowKey = dataset.getRowKey(row);
                double xx = domainAxis.getCategorySeriesMiddle(columnKey, rowKey, dataset, this.getItemMargin(), dataArea, plot.getDomainAxisEdge());
                this.updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value1.doubleValue(), datasetIndex, xx, translatedValue1, orientation);
            }
            if (state.getInfo() == null || (entities = state.getEntityCollection()) == null) continue;
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }

    protected void drawTask(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, GanttCategoryDataset dataset, int row, int column) {
        CategoryItemLabelGenerator generator;
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        Number value0 = dataset.getEndValue(row, column);
        if (value0 == null) {
            return;
        }
        double java2dValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
        Number value1 = dataset.getStartValue(row, column);
        if (value1 == null) {
            return;
        }
        double java2dValue1 = rangeAxis.valueToJava2D(value1.doubleValue(), dataArea, rangeAxisLocation);
        if (java2dValue1 < java2dValue0) {
            double temp = java2dValue1;
            java2dValue1 = java2dValue0;
            java2dValue0 = temp;
            Number tempNum = value1;
            value1 = value0;
            value0 = tempNum;
        }
        double rectStart = this.calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
        double rectBreadth = state.getBarWidth();
        double rectLength = Math.abs(java2dValue1 - java2dValue0);
        Rectangle2D.Double bar = null;
        RectangleEdge barBase = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(java2dValue0, rectStart, rectLength, rectBreadth);
            barBase = RectangleEdge.LEFT;
        } else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(rectStart, java2dValue1, rectBreadth, rectLength);
            barBase = RectangleEdge.BOTTOM;
        }
        Rectangle2D.Double completeBar = null;
        Rectangle2D.Double incompleteBar = null;
        Number percent = dataset.getPercentComplete(row, column);
        double start = this.getStartPercent();
        double end = this.getEndPercent();
        if (percent != null) {
            double p = percent.doubleValue();
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                completeBar = new Rectangle2D.Double(java2dValue0, rectStart + start * rectBreadth, rectLength * p, rectBreadth * (end - start));
                incompleteBar = new Rectangle2D.Double(java2dValue0 + rectLength * p, rectStart + start * rectBreadth, rectLength * (1.0 - p), rectBreadth * (end - start));
            } else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                completeBar = new Rectangle2D.Double(rectStart + start * rectBreadth, java2dValue1 + rectLength * (1.0 - p), rectBreadth * (end - start), rectLength * p);
                incompleteBar = new Rectangle2D.Double(rectStart + start * rectBreadth, java2dValue1, rectBreadth * (end - start), rectLength * (1.0 - p));
            }
        }
        if (this.getShadowsVisible()) {
            this.getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase, true);
        }
        this.getBarPainter().paintBar(g2, this, row, column, bar, barBase);
        if (completeBar != null) {
            g2.setPaint(this.getCompletePaint());
            g2.fill(completeBar);
        }
        if (incompleteBar != null) {
            g2.setPaint(this.getIncompletePaint());
            g2.fill(incompleteBar);
        }
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
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, false);
        }
        int datasetIndex = plot.indexOf(dataset);
        Comparable columnKey = dataset.getColumnKey(column);
        Comparable rowKey = dataset.getRowKey(row);
        double xx = domainAxis.getCategorySeriesMiddle(columnKey, rowKey, dataset, this.getItemMargin(), dataArea, plot.getDomainAxisEdge());
        this.updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value1.doubleValue(), datasetIndex, xx, java2dValue1, orientation);
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }

    public double getItemMiddle(Comparable rowKey, Comparable columnKey, CategoryDataset dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge) {
        return axis.getCategorySeriesMiddle(columnKey, rowKey, dataset, this.getItemMargin(), area, edge);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GanttRenderer)) {
            return false;
        }
        GanttRenderer that = (GanttRenderer)obj;
        if (!PaintUtilities.equal(this.completePaint, that.completePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.incompletePaint, that.incompletePaint)) {
            return false;
        }
        if (this.startPercent != that.startPercent) {
            return false;
        }
        if (this.endPercent != that.endPercent) {
            return false;
        }
        return super.equals(obj);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.completePaint, stream);
        SerialUtilities.writePaint(this.incompletePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.completePaint = SerialUtilities.readPaint(stream);
        this.incompletePaint = SerialUtilities.readPaint(stream);
    }
}

