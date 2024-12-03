/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.jfree.data.DomainInfo;
import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ObjectUtilities;

public class TimeSeriesCollection
extends AbstractIntervalXYDataset
implements XYDataset,
IntervalXYDataset,
DomainInfo,
Serializable {
    private static final long serialVersionUID = 834149929022371137L;
    private List data;
    private Calendar workingCalendar;
    private TimePeriodAnchor xPosition;
    private boolean domainIsPointsInTime;

    public TimeSeriesCollection() {
        this(null, TimeZone.getDefault());
    }

    public TimeSeriesCollection(TimeZone zone) {
        this(null, zone);
    }

    public TimeSeriesCollection(TimeSeries series) {
        this(series, TimeZone.getDefault());
    }

    public TimeSeriesCollection(TimeSeries series, TimeZone zone) {
        if (zone == null) {
            zone = TimeZone.getDefault();
        }
        this.workingCalendar = Calendar.getInstance(zone);
        this.data = new ArrayList();
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
        this.xPosition = TimePeriodAnchor.START;
        this.domainIsPointsInTime = true;
    }

    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    public void setXPosition(TimePeriodAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.xPosition = anchor;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    public List getSeries() {
        return Collections.unmodifiableList(this.data);
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public int indexOf(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        return this.data.indexOf(series);
    }

    public TimeSeries getSeries(int series) {
        if (series < 0 || series >= this.getSeriesCount()) {
            throw new IllegalArgumentException("The 'series' argument is out of bounds (" + series + ").");
        }
        return (TimeSeries)this.data.get(series);
    }

    public TimeSeries getSeries(Comparable key) {
        TimeSeries result = null;
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            TimeSeries series = (TimeSeries)iterator.next();
            Comparable k = series.getKey();
            if (k == null || !k.equals(key)) continue;
            result = series;
        }
        return result;
    }

    public Comparable getSeriesKey(int series) {
        return this.getSeries(series).getKey();
    }

    public void addSeries(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        this.fireDatasetChanged();
    }

    public void removeSeries(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.remove(series);
        series.removeChangeListener(this);
        this.fireDatasetChanged();
    }

    public void removeSeries(int index) {
        TimeSeries series = this.getSeries(index);
        if (series != null) {
            this.removeSeries(series);
        }
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); ++i) {
            TimeSeries series = (TimeSeries)this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        this.fireDatasetChanged();
    }

    public int getItemCount(int series) {
        return this.getSeries(series).getItemCount();
    }

    public double getXValue(int series, int item) {
        TimeSeries s = (TimeSeries)this.data.get(series);
        TimeSeriesDataItem i = s.getDataItem(item);
        RegularTimePeriod period = i.getPeriod();
        return this.getX(period);
    }

    public Number getX(int series, int item) {
        TimeSeries ts = (TimeSeries)this.data.get(series);
        TimeSeriesDataItem dp = ts.getDataItem(item);
        RegularTimePeriod period = dp.getPeriod();
        return new Long(this.getX(period));
    }

    protected synchronized long getX(RegularTimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond(this.workingCalendar);
        } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond(this.workingCalendar);
        } else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond(this.workingCalendar);
        }
        return result;
    }

    public synchronized Number getStartX(int series, int item) {
        TimeSeries ts = (TimeSeries)this.data.get(series);
        TimeSeriesDataItem dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getFirstMillisecond(this.workingCalendar));
    }

    public synchronized Number getEndX(int series, int item) {
        TimeSeries ts = (TimeSeries)this.data.get(series);
        TimeSeriesDataItem dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getLastMillisecond(this.workingCalendar));
    }

    public Number getY(int series, int item) {
        TimeSeries ts = (TimeSeries)this.data.get(series);
        TimeSeriesDataItem dp = ts.getDataItem(item);
        return dp.getValue();
    }

    public Number getStartY(int series, int item) {
        return this.getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return this.getY(series, item);
    }

    public int[] getSurroundingItems(int series, long milliseconds) {
        int[] result = new int[]{-1, -1};
        TimeSeries timeSeries = this.getSeries(series);
        for (int i = 0; i < timeSeries.getItemCount(); ++i) {
            Number x = this.getX(series, i);
            long m = x.longValue();
            if (m <= milliseconds) {
                result[0] = i;
            }
            if (m < milliseconds) continue;
            result[1] = i;
            break;
        }
        return result;
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
        Range result = null;
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            TimeSeries series = (TimeSeries)iterator.next();
            int count = series.getItemCount();
            if (count <= 0) continue;
            RegularTimePeriod start = series.getTimePeriod(0);
            RegularTimePeriod end = series.getTimePeriod(count - 1);
            Range temp = !includeInterval ? new Range(this.getX(start), this.getX(end)) : new Range(start.getFirstMillisecond(this.workingCalendar), end.getLastMillisecond(this.workingCalendar));
            result = Range.combine(result, temp);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeSeriesCollection)) {
            return false;
        }
        TimeSeriesCollection that = (TimeSeriesCollection)obj;
        if (this.xPosition != that.xPosition) {
            return false;
        }
        if (this.domainIsPointsInTime != that.domainIsPointsInTime) {
            return false;
        }
        return ObjectUtilities.equal(this.data, that.data);
    }

    public int hashCode() {
        int result = ((Object)this.data).hashCode();
        result = 29 * result + (this.workingCalendar != null ? this.workingCalendar.hashCode() : 0);
        result = 29 * result + (this.xPosition != null ? this.xPosition.hashCode() : 0);
        result = 29 * result + (this.domainIsPointsInTime ? 1 : 0);
        return result;
    }
}

