/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.IntervalXYDelegate;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class DefaultTableXYDataset
extends AbstractIntervalXYDataset
implements TableXYDataset,
IntervalXYDataset,
DomainInfo,
PublicCloneable {
    private List data = null;
    private HashSet xPoints = null;
    private boolean propagateEvents = true;
    private boolean autoPrune = false;
    private IntervalXYDelegate intervalDelegate;

    public DefaultTableXYDataset() {
        this(false);
    }

    public DefaultTableXYDataset(boolean autoPrune) {
        this.autoPrune = autoPrune;
        this.data = new ArrayList();
        this.xPoints = new HashSet();
        this.intervalDelegate = new IntervalXYDelegate(this, false);
        this.addChangeListener(this.intervalDelegate);
    }

    public boolean isAutoPrune() {
        return this.autoPrune;
    }

    public void addSeries(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        if (series.getAllowDuplicateXValues()) {
            throw new IllegalArgumentException("Cannot accept XYSeries that allow duplicate values. Use XYSeries(seriesName, <sort>, false) constructor.");
        }
        this.updateXPoints(series);
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }

    private void updateXPoints(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' not permitted.");
        }
        HashSet<Number> seriesXPoints = new HashSet<Number>();
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int itemNo = 0; itemNo < series.getItemCount(); ++itemNo) {
            Number xValue = series.getX(itemNo);
            seriesXPoints.add(xValue);
            if (this.xPoints.contains(xValue)) continue;
            this.xPoints.add(xValue);
            int seriesCount = this.data.size();
            for (int seriesNo = 0; seriesNo < seriesCount; ++seriesNo) {
                XYSeries dataSeries = (XYSeries)this.data.get(seriesNo);
                if (dataSeries.equals(series)) continue;
                dataSeries.add(xValue, null);
            }
        }
        Iterator iterator = this.xPoints.iterator();
        while (iterator.hasNext()) {
            Number xPoint = (Number)iterator.next();
            if (seriesXPoints.contains(xPoint)) continue;
            series.add(xPoint, null);
        }
        this.propagateEvents = savedState;
    }

    public void updateXPoints() {
        this.propagateEvents = false;
        for (int s = 0; s < this.data.size(); ++s) {
            this.updateXPoints((XYSeries)this.data.get(s));
        }
        if (this.autoPrune) {
            this.prune();
        }
        this.propagateEvents = true;
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public int getItemCount() {
        if (this.xPoints == null) {
            return 0;
        }
        return this.xPoints.size();
    }

    public XYSeries getSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Index outside valid range.");
        }
        return (XYSeries)this.data.get(series);
    }

    public Comparable getSeriesKey(int series) {
        return this.getSeries(series).getKey();
    }

    public int getItemCount(int series) {
        return this.getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        XYSeries s = (XYSeries)this.data.get(series);
        XYDataItem dataItem = s.getDataItem(item);
        return dataItem.getX();
    }

    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    public Number getY(int series, int index) {
        XYSeries ts = (XYSeries)this.data.get(series);
        XYDataItem dataItem = ts.getDataItem(index);
        return dataItem.getY();
    }

    public Number getStartY(int series, int item) {
        return this.getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return this.getY(series, item);
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); ++i) {
            XYSeries series = (XYSeries)this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.xPoints.clear();
        this.fireDatasetChanged();
    }

    public void removeSeries(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            if (this.data.size() == 0) {
                this.xPoints.clear();
            }
            this.fireDatasetChanged();
        }
    }

    public void removeSeries(int series) {
        if (series < 0 || series > this.getSeriesCount()) {
            throw new IllegalArgumentException("Index outside valid range.");
        }
        XYSeries s = (XYSeries)this.data.get(series);
        s.removeChangeListener(this);
        this.data.remove(series);
        if (this.data.size() == 0) {
            this.xPoints.clear();
        } else if (this.autoPrune) {
            this.prune();
        }
        this.fireDatasetChanged();
    }

    public void removeAllValuesForX(Number x) {
        if (x == null) {
            throw new IllegalArgumentException("Null 'x' argument.");
        }
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int s = 0; s < this.data.size(); ++s) {
            XYSeries series = (XYSeries)this.data.get(s);
            series.remove(x);
        }
        this.propagateEvents = savedState;
        this.xPoints.remove(x);
        this.fireDatasetChanged();
    }

    protected boolean canPrune(Number x) {
        for (int s = 0; s < this.data.size(); ++s) {
            XYSeries series = (XYSeries)this.data.get(s);
            if (series.getY(series.indexOf(x)) == null) continue;
            return false;
        }
        return true;
    }

    public void prune() {
        HashSet hs = (HashSet)this.xPoints.clone();
        Iterator iterator = hs.iterator();
        while (iterator.hasNext()) {
            Number x = (Number)iterator.next();
            if (!this.canPrune(x)) continue;
            this.removeAllValuesForX(x);
        }
    }

    public void seriesChanged(SeriesChangeEvent event) {
        if (this.propagateEvents) {
            this.updateXPoints();
            this.fireDatasetChanged();
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultTableXYDataset)) {
            return false;
        }
        DefaultTableXYDataset that = (DefaultTableXYDataset)obj;
        if (this.autoPrune != that.autoPrune) {
            return false;
        }
        if (this.propagateEvents != that.propagateEvents) {
            return false;
        }
        if (!this.intervalDelegate.equals(that.intervalDelegate)) {
            return false;
        }
        return ObjectUtilities.equal(this.data, that.data);
    }

    public int hashCode() {
        int result = this.data != null ? ((Object)this.data).hashCode() : 0;
        result = 29 * result + (this.xPoints != null ? this.xPoints.hashCode() : 0);
        result = 29 * result + (this.propagateEvents ? 1 : 0);
        result = 29 * result + (this.autoPrune ? 1 : 0);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultTableXYDataset clone = (DefaultTableXYDataset)super.clone();
        int seriesCount = this.data.size();
        clone.data = new ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; ++i) {
            XYSeries series = (XYSeries)this.data.get(i);
            clone.data.add(series.clone());
        }
        clone.intervalDelegate = new IntervalXYDelegate(clone);
        clone.intervalDelegate.setFixedIntervalWidth(this.getIntervalWidth());
        clone.intervalDelegate.setAutoWidth(this.isAutoWidth());
        clone.intervalDelegate.setIntervalPositionFactor(this.getIntervalPositionFactor());
        clone.updateXPoints();
        return clone;
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
}

