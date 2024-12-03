/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.HashUtilities;
import org.jfree.data.DomainInfo;
import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.IntervalXYDelegate;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYSeriesCollection
extends AbstractIntervalXYDataset
implements IntervalXYDataset,
DomainInfo,
RangeInfo,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -7590013825931496766L;
    private List data = new ArrayList();
    private IntervalXYDelegate intervalDelegate = new IntervalXYDelegate(this, false);

    public XYSeriesCollection() {
        this(null);
    }

    public XYSeriesCollection(XYSeries series) {
        this.addChangeListener(this.intervalDelegate);
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
    }

    public DomainOrder getDomainOrder() {
        int seriesCount = this.getSeriesCount();
        for (int i = 0; i < seriesCount; ++i) {
            XYSeries s = this.getSeries(i);
            if (s.getAutoSort()) continue;
            return DomainOrder.NONE;
        }
        return DomainOrder.ASCENDING;
    }

    public void addSeries(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }

    public void removeSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        XYSeries ts = (XYSeries)this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        this.fireDatasetChanged();
    }

    public void removeSeries(XYSeries series) {
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
            XYSeries series = (XYSeries)this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public List getSeries() {
        return Collections.unmodifiableList(this.data);
    }

    public int indexOf(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        return this.data.indexOf(series);
    }

    public XYSeries getSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (XYSeries)this.data.get(series);
    }

    public XYSeries getSeries(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            XYSeries series = (XYSeries)iterator.next();
            if (!key.equals(series.getKey())) continue;
            return series;
        }
        throw new UnknownKeyException("Key not found: " + key);
    }

    public Comparable getSeriesKey(int series) {
        return this.getSeries(series).getKey();
    }

    public int getItemCount(int series) {
        return this.getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        XYSeries ts = (XYSeries)this.data.get(series);
        XYDataItem xyItem = ts.getDataItem(item);
        return xyItem.getX();
    }

    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    public Number getY(int series, int index) {
        XYSeries ts = (XYSeries)this.data.get(series);
        XYDataItem xyItem = ts.getDataItem(index);
        return xyItem.getY();
    }

    public Number getStartY(int series, int item) {
        return this.getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return this.getY(series, item);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeriesCollection)) {
            return false;
        }
        XYSeriesCollection that = (XYSeriesCollection)obj;
        if (!this.intervalDelegate.equals(that.intervalDelegate)) {
            return false;
        }
        return ObjectUtilities.equal(this.data, that.data);
    }

    public Object clone() throws CloneNotSupportedException {
        XYSeriesCollection clone = (XYSeriesCollection)super.clone();
        clone.data = (List)ObjectUtilities.deepClone(this.data);
        clone.intervalDelegate = (IntervalXYDelegate)this.intervalDelegate.clone();
        return clone;
    }

    public int hashCode() {
        int hash = 5;
        hash = HashUtilities.hashCode(hash, this.intervalDelegate);
        hash = HashUtilities.hashCode(hash, this.data);
        return hash;
    }

    public double getDomainLowerBound(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainLowerBound(includeInterval);
        }
        double result = Double.NaN;
        int seriesCount = this.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            XYSeries series = this.getSeries(s);
            double lowX = series.getMinX();
            if (Double.isNaN(result)) {
                result = lowX;
                continue;
            }
            if (Double.isNaN(lowX)) continue;
            result = Math.min(result, lowX);
        }
        return result;
    }

    public double getDomainUpperBound(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainUpperBound(includeInterval);
        }
        double result = Double.NaN;
        int seriesCount = this.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            XYSeries series = this.getSeries(s);
            double hiX = series.getMaxX();
            if (Double.isNaN(result)) {
                result = hiX;
                continue;
            }
            if (Double.isNaN(hiX)) continue;
            result = Math.max(result, hiX);
        }
        return result;
    }

    public Range getDomainBounds(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainBounds(includeInterval);
        }
        double lower = Double.POSITIVE_INFINITY;
        double upper = Double.NEGATIVE_INFINITY;
        int seriesCount = this.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            double maxX;
            XYSeries series = this.getSeries(s);
            double minX = series.getMinX();
            if (!Double.isNaN(minX)) {
                lower = Math.min(lower, minX);
            }
            if (Double.isNaN(maxX = series.getMaxX())) continue;
            upper = Math.max(upper, maxX);
        }
        if (lower > upper) {
            return null;
        }
        return new Range(lower, upper);
    }

    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }

    public void setIntervalWidth(double width) {
        if (width < 0.0) {
            throw new IllegalArgumentException("Negative 'width' argument.");
        }
        this.intervalDelegate.setFixedIntervalWidth(width);
        this.fireDatasetChanged();
    }

    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }

    public void setIntervalPositionFactor(double factor) {
        this.intervalDelegate.setIntervalPositionFactor(factor);
        this.fireDatasetChanged();
    }

    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        this.fireDatasetChanged();
    }

    public Range getRangeBounds(boolean includeInterval) {
        double lower = Double.POSITIVE_INFINITY;
        double upper = Double.NEGATIVE_INFINITY;
        int seriesCount = this.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            double maxY;
            XYSeries series = this.getSeries(s);
            double minY = series.getMinY();
            if (!Double.isNaN(minY)) {
                lower = Math.min(lower, minY);
            }
            if (Double.isNaN(maxY = series.getMaxY())) continue;
            upper = Math.max(upper, maxY);
        }
        if (lower > upper) {
            return null;
        }
        return new Range(lower, upper);
    }

    public double getRangeLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        int seriesCount = this.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            XYSeries series = this.getSeries(s);
            double lowY = series.getMinY();
            if (Double.isNaN(result)) {
                result = lowY;
                continue;
            }
            if (Double.isNaN(lowY)) continue;
            result = Math.min(result, lowY);
        }
        return result;
    }

    public double getRangeUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        int seriesCount = this.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            XYSeries series = this.getSeries(s);
            double hiY = series.getMaxY();
            if (Double.isNaN(result)) {
                result = hiY;
                continue;
            }
            if (Double.isNaN(hiY)) continue;
            result = Math.max(result, hiY);
        }
        return result;
    }
}

