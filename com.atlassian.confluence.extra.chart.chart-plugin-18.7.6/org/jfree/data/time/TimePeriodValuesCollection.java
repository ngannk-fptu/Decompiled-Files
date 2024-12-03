/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.ObjectUtilities;

public class TimePeriodValuesCollection
extends AbstractIntervalXYDataset
implements IntervalXYDataset,
DomainInfo,
Serializable {
    private static final long serialVersionUID = -3077934065236454199L;
    private List data = new ArrayList();
    private TimePeriodAnchor xPosition = TimePeriodAnchor.MIDDLE;
    private boolean domainIsPointsInTime = false;

    public TimePeriodValuesCollection() {
        this(null);
    }

    public TimePeriodValuesCollection(TimePeriodValues series) {
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
    }

    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    public void setXPosition(TimePeriodAnchor position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.xPosition = position;
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public TimePeriodValues getSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("Index 'series' out of range.");
        }
        return (TimePeriodValues)this.data.get(series);
    }

    public Comparable getSeriesKey(int series) {
        return this.getSeries(series).getKey();
    }

    public void addSeries(TimePeriodValues series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }

    public void removeSeries(TimePeriodValues series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.remove(series);
        series.removeChangeListener(this);
        this.fireDatasetChanged();
    }

    public void removeSeries(int index) {
        TimePeriodValues series = this.getSeries(index);
        if (series != null) {
            this.removeSeries(series);
        }
    }

    public int getItemCount(int series) {
        return this.getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        TimePeriod period = dp.getPeriod();
        return new Long(this.getX(period));
    }

    private long getX(TimePeriod period) {
        if (this.xPosition == TimePeriodAnchor.START) {
            return period.getStart().getTime();
        }
        if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            return period.getStart().getTime() / 2L + period.getEnd().getTime() / 2L;
        }
        if (this.xPosition == TimePeriodAnchor.END) {
            return period.getEnd().getTime();
        }
        throw new IllegalStateException("TimePeriodAnchor unknown.");
    }

    public Number getStartX(int series, int item) {
        TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getStart().getTime());
    }

    public Number getEndX(int series, int item) {
        TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getEnd().getTime());
    }

    public Number getY(int series, int item) {
        TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        return dp.getValue();
    }

    public Number getStartY(int series, int item) {
        return this.getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return this.getY(series, item);
    }

    public double getDomainLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        Range r = this.getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getLowerBound();
        }
        return result;
    }

    public double getDomainUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        Range r = this.getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getUpperBound();
        }
        return result;
    }

    public Range getDomainBounds(boolean includeInterval) {
        boolean interval = includeInterval || this.domainIsPointsInTime;
        Range result = null;
        Range temp = null;
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            TimePeriodValues series = (TimePeriodValues)iterator.next();
            int count = series.getItemCount();
            if (count <= 0) continue;
            TimePeriod start = series.getTimePeriod(series.getMinStartIndex());
            TimePeriod end = series.getTimePeriod(series.getMaxEndIndex());
            if (!interval) {
                if (this.xPosition == TimePeriodAnchor.START) {
                    TimePeriod maxStart = series.getTimePeriod(series.getMaxStartIndex());
                    temp = new Range(start.getStart().getTime(), maxStart.getStart().getTime());
                } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
                    TimePeriod minMiddle = series.getTimePeriod(series.getMinMiddleIndex());
                    long s1 = minMiddle.getStart().getTime();
                    long e1 = minMiddle.getEnd().getTime();
                    TimePeriod maxMiddle = series.getTimePeriod(series.getMaxMiddleIndex());
                    long s2 = maxMiddle.getStart().getTime();
                    long e2 = maxMiddle.getEnd().getTime();
                    temp = new Range(s1 + (e1 - s1) / 2L, s2 + (e2 - s2) / 2L);
                } else if (this.xPosition == TimePeriodAnchor.END) {
                    TimePeriod minEnd = series.getTimePeriod(series.getMinEndIndex());
                    temp = new Range(minEnd.getEnd().getTime(), end.getEnd().getTime());
                }
            } else {
                temp = new Range(start.getStart().getTime(), end.getEnd().getTime());
            }
            result = Range.combine(result, temp);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimePeriodValuesCollection)) {
            return false;
        }
        TimePeriodValuesCollection that = (TimePeriodValuesCollection)obj;
        if (this.domainIsPointsInTime != that.domainIsPointsInTime) {
            return false;
        }
        if (this.xPosition != that.xPosition) {
            return false;
        }
        return ObjectUtilities.equal(this.data, that.data);
    }

    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
    }
}

