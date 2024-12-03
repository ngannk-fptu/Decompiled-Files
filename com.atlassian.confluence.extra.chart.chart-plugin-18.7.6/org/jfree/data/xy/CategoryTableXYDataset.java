/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.IntervalXYDelegate;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.util.PublicCloneable;

public class CategoryTableXYDataset
extends AbstractIntervalXYDataset
implements TableXYDataset,
IntervalXYDataset,
DomainInfo,
PublicCloneable {
    private DefaultKeyedValues2D values = new DefaultKeyedValues2D(true);
    private IntervalXYDelegate intervalDelegate = new IntervalXYDelegate(this);

    public CategoryTableXYDataset() {
        this.addChangeListener(this.intervalDelegate);
    }

    public void add(double x, double y, String seriesName) {
        this.add(new Double(x), new Double(y), seriesName, true);
    }

    public void add(Number x, Number y, String seriesName, boolean notify) {
        this.values.addValue(y, (Comparable)((Object)x), (Comparable)((Object)seriesName));
        if (notify) {
            this.fireDatasetChanged();
        }
    }

    public void remove(double x, String seriesName) {
        this.remove(new Double(x), seriesName, true);
    }

    public void remove(Number x, String seriesName, boolean notify) {
        this.values.removeValue((Comparable)((Object)x), (Comparable)((Object)seriesName));
        if (notify) {
            this.fireDatasetChanged();
        }
    }

    public int getSeriesCount() {
        return this.values.getColumnCount();
    }

    public Comparable getSeriesKey(int series) {
        return this.values.getColumnKey(series);
    }

    public int getItemCount() {
        return this.values.getRowCount();
    }

    public int getItemCount(int series) {
        return this.getItemCount();
    }

    public Number getX(int series, int item) {
        return (Number)((Object)this.values.getRowKey(item));
    }

    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    public Number getY(int series, int item) {
        return this.values.getValue(item, series);
    }

    public Number getStartY(int series, int item) {
        return this.getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return this.getY(series, item);
    }

    public double getDomainLowerBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainLowerBound(includeInterval);
    }

    public double getDomainUpperBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainUpperBound(includeInterval);
    }

    public Range getDomainBounds(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainBounds(includeInterval);
        }
        return DatasetUtilities.iterateDomainBounds(this, includeInterval);
    }

    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }

    public void setIntervalPositionFactor(double d) {
        this.intervalDelegate.setIntervalPositionFactor(d);
        this.fireDatasetChanged();
    }

    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }

    public void setIntervalWidth(double d) {
        this.intervalDelegate.setFixedIntervalWidth(d);
        this.fireDatasetChanged();
    }

    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        this.fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CategoryTableXYDataset)) {
            return false;
        }
        CategoryTableXYDataset that = (CategoryTableXYDataset)obj;
        if (!this.intervalDelegate.equals(that.intervalDelegate)) {
            return false;
        }
        return this.values.equals(that.values);
    }

    public Object clone() throws CloneNotSupportedException {
        CategoryTableXYDataset clone = (CategoryTableXYDataset)super.clone();
        clone.values = (DefaultKeyedValues2D)this.values.clone();
        clone.intervalDelegate = new IntervalXYDelegate(clone);
        clone.intervalDelegate.setFixedIntervalWidth(this.getIntervalWidth());
        clone.intervalDelegate.setAutoWidth(this.isAutoWidth());
        clone.intervalDelegate.setIntervalPositionFactor(this.getIntervalPositionFactor());
        return clone;
    }
}

