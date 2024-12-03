/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class YIntervalSeriesCollection
extends AbstractIntervalXYDataset
implements IntervalXYDataset,
PublicCloneable,
Serializable {
    private List data = new ArrayList();

    public void addSeries(YIntervalSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public YIntervalSeries getSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (YIntervalSeries)this.data.get(series);
    }

    public Comparable getSeriesKey(int series) {
        return this.getSeries(series).getKey();
    }

    public int getItemCount(int series) {
        return this.getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        YIntervalSeries s = (YIntervalSeries)this.data.get(series);
        return s.getX(item);
    }

    public double getYValue(int series, int item) {
        YIntervalSeries s = (YIntervalSeries)this.data.get(series);
        return s.getYValue(item);
    }

    public double getStartYValue(int series, int item) {
        YIntervalSeries s = (YIntervalSeries)this.data.get(series);
        return s.getYLowValue(item);
    }

    public double getEndYValue(int series, int item) {
        YIntervalSeries s = (YIntervalSeries)this.data.get(series);
        return s.getYHighValue(item);
    }

    public Number getY(int series, int item) {
        YIntervalSeries s = (YIntervalSeries)this.data.get(series);
        return new Double(s.getYValue(item));
    }

    public Number getStartX(int series, int item) {
        return this.getX(series, item);
    }

    public Number getEndX(int series, int item) {
        return this.getX(series, item);
    }

    public Number getStartY(int series, int item) {
        YIntervalSeries s = (YIntervalSeries)this.data.get(series);
        return new Double(s.getYLowValue(item));
    }

    public Number getEndY(int series, int item) {
        YIntervalSeries s = (YIntervalSeries)this.data.get(series);
        return new Double(s.getYHighValue(item));
    }

    public void removeSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        YIntervalSeries ts = (YIntervalSeries)this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        this.fireDatasetChanged();
    }

    public void removeSeries(YIntervalSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            this.fireDatasetChanged();
        }
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); ++i) {
            YIntervalSeries series = (YIntervalSeries)this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof YIntervalSeriesCollection)) {
            return false;
        }
        YIntervalSeriesCollection that = (YIntervalSeriesCollection)obj;
        return ObjectUtilities.equal(this.data, that.data);
    }

    public Object clone() throws CloneNotSupportedException {
        YIntervalSeriesCollection clone = (YIntervalSeriesCollection)super.clone();
        clone.data = (List)ObjectUtilities.deepClone(this.data);
        return clone;
    }
}

