/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class GroupedStackedBarRenderer
extends StackedBarRenderer
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -2725921399005922939L;
    private KeyToGroupMap seriesToGroupMap = new KeyToGroupMap();

    public void setSeriesToGroupMap(KeyToGroupMap map) {
        if (map == null) {
            throw new IllegalArgumentException("Null 'map' argument.");
        }
        this.seriesToGroupMap = map;
        this.fireChangeEvent();
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        Range r = DatasetUtilities.findStackedRangeBounds(dataset, this.seriesToGroupMap);
        return r;
    }

    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        CategoryAxis xAxis = plot.getDomainAxisForDataset(rendererIndex);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            PlotOrientation orientation = plot.getOrientation();
            double space = 0.0;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            } else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * this.getMaximumBarWidth();
            int groups = this.seriesToGroupMap.getGroupCount();
            int categories = data.getColumnCount();
            int columns = groups * categories;
            double categoryMargin = 0.0;
            double itemMargin = 0.0;
            if (categories > 1) {
                categoryMargin = xAxis.getCategoryMargin();
            }
            if (groups > 1) {
                itemMargin = this.getItemMargin();
            }
            double used = space * (1.0 - xAxis.getLowerMargin() - xAxis.getUpperMargin() - categoryMargin - itemMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / (double)columns, maxWidth));
            } else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    protected double calculateBarW0(CategoryPlot plot, PlotOrientation orientation, Rectangle2D dataArea, CategoryAxis domainAxis, CategoryItemRendererState state, int row, int column) {
        double space = 0.0;
        space = orientation == PlotOrientation.HORIZONTAL ? dataArea.getHeight() : dataArea.getWidth();
        double barW0 = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        int groupCount = this.seriesToGroupMap.getGroupCount();
        int groupIndex = this.seriesToGroupMap.getGroupIndex(this.seriesToGroupMap.getGroup(plot.getDataset(plot.getIndexOf(this)).getRowKey(row)));
        int categoryCount = this.getColumnCount();
        if (groupCount > 1) {
            double groupGap = space * this.getItemMargin() / (double)(categoryCount * (groupCount - 1));
            double groupW = this.calculateSeriesWidth(space, domainAxis, categoryCount, groupCount);
            barW0 = barW0 + (double)groupIndex * (groupW + groupGap) + groupW / 2.0 - state.getBarWidth() / 2.0;
        } else {
            barW0 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        }
        return barW0;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        EntityCollection entities;
        double translatedValue;
        double translatedBase;
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        double value = dataValue.doubleValue();
        Comparable group = this.seriesToGroupMap.getGroup(dataset.getRowKey(row));
        PlotOrientation orientation = plot.getOrientation();
        double barW0 = this.calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
        double positiveBase = 0.0;
        double negativeBase = 0.0;
        for (int i = 0; i < row; ++i) {
            Number v;
            if (!group.equals(this.seriesToGroupMap.getGroup(dataset.getRowKey(i))) || (v = dataset.getValue(i, column)) == null) continue;
            double d = v.doubleValue();
            if (d > 0.0) {
                positiveBase += d;
                continue;
            }
            negativeBase += d;
        }
        boolean positive = value > 0.0;
        boolean inverted = rangeAxis.isInverted();
        RectangleEdge barBase = orientation == PlotOrientation.HORIZONTAL ? (positive && inverted || !positive && !inverted ? RectangleEdge.RIGHT : RectangleEdge.LEFT) : (positive && !inverted || !positive && inverted ? RectangleEdge.BOTTOM : RectangleEdge.TOP);
        RectangleEdge location = plot.getRangeAxisEdge();
        if (value > 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea, location);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value, dataArea, location);
        } else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea, location);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value, dataArea, location);
        }
        double barL0 = Math.min(translatedBase, translatedValue);
        double barLength = Math.max(Math.abs(translatedValue - translatedBase), this.getMinimumBarLength());
        Rectangle2D.Double bar = null;
        bar = orientation == PlotOrientation.HORIZONTAL ? new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth()) : new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
        this.getBarPainter().paintBar(g2, this, row, column, bar, barBase);
        CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0);
        }
        if (state.getInfo() != null && (entities = state.getEntityCollection()) != null) {
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GroupedStackedBarRenderer)) {
            return false;
        }
        GroupedStackedBarRenderer that = (GroupedStackedBarRenderer)obj;
        if (!this.seriesToGroupMap.equals(that.seriesToGroupMap)) {
            return false;
        }
        return super.equals(obj);
    }
}

