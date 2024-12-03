/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYIntervalSeriesCollection
extends AbstractIntervalXYDataset
implements IntervalXYDataset,
PublicCloneable,
Serializable {
    private List data = new ArrayList();

    public void addSeries(XYIntervalSeries series) {
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

    public XYIntervalSeries getSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (XYIntervalSeries)this.data.get(series);
    }

    public Comparable getSeriesKey(int series) {
        return this.getSeries(series).getKey();
    }

    public int getItemCount(int series) {
        return this.getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
        return s.getX(item);
    }

    public double getStartXValue(int series, int item) {
        XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
        return s.getXLowValue(item);
    }

    public double getEndXValue(int series, int item) {
        XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
        return s.getXHighValue(item);
    }

    public double getYValue(int series, int item) {
        XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
        return s.getYValue(item);
    }

    public double getStartYValue(int series, int item) {
        XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
        return s.getYLowValue(item);
    }

    public double getEndYValue(int series, int item) {
        XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
        return s.getYHighValue(item);
    }

    public Number getY(int series, int item) {
        return new Double(this.getYValue(series, item));
    }

    public Number getStartX(int series, int item) {
        return new Double(this.getStartXValue(series, item));
    }

    public Number getEndX(int series, int item) {
        return new Double(this.getEndXValue(series, item));
    }

    public Number getStartY(int series, int item) {
        return new Double(this.getStartYValue(series, item));
    }

    public Number getEndY(int series, int item) {
        return new Double(this.getEndYValue(series, item));
    }

    public void removeSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        XYIntervalSeries ts = (XYIntervalSeries)this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        this.fireDatasetChanged();
    }

    public void removeSeries(XYIntervalSeries series) {
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
            XYIntervalSeries series = (XYIntervalSeries)this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYIntervalSeriesCollection)) {
            return false;
        }
        XYIntervalSeriesCollection that = (XYIntervalSeriesCollection)obj;
        return ObjectUtilities.equal(this.data, that.data);
    }

    public Object clone() throws CloneNotSupportedException {
        XYIntervalSeriesCollection clone = (XYIntervalSeriesCollection)super.clone();
        int seriesCount = this.getSeriesCount();
        clone.data = new ArrayList(seriesCount);
        for (int i = 0; i < this.data.size(); ++i) {
            clone.data.set(i, this.getSeries(i).clone());
        }
        return clone;
    }
}

