/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.util.PublicCloneable;

public class TimeTableXYDataset
extends AbstractIntervalXYDataset
implements Cloneable,
PublicCloneable,
IntervalXYDataset,
DomainInfo,
TableXYDataset {
    private DefaultKeyedValues2D values;
    private boolean domainIsPointsInTime;
    private TimePeriodAnchor xPosition;
    private Calendar workingCalendar;

    public TimeTableXYDataset() {
        this(TimeZone.getDefault(), Locale.getDefault());
    }

    public TimeTableXYDataset(TimeZone zone) {
        this(zone, Locale.getDefault());
    }

    public TimeTableXYDataset(TimeZone zone, Locale locale) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Null 'locale' argument.");
        }
        this.values = new DefaultKeyedValues2D(true);
        this.workingCalendar = Calendar.getInstance(zone, locale);
        this.xPosition = TimePeriodAnchor.START;
    }

    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
        this.notifyListeners(new DatasetChangeEvent(this, this));
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

    public void add(TimePeriod period, double y, String seriesName) {
        this.add(period, new Double(y), seriesName, true);
    }

    public void add(TimePeriod period, Number y, String seriesName, boolean notify) {
        this.values.addValue(y, period, (Comparable)((Object)seriesName));
        if (notify) {
            this.fireDatasetChanged();
        }
    }

    public void remove(TimePeriod period, String seriesName) {
        this.remove(period, seriesName, true);
    }

    public void remove(TimePeriod period, String seriesName, boolean notify) {
        this.values.removeValue(period, (Comparable)((Object)seriesName));
        if (notify) {
            this.fireDatasetChanged();
        }
    }

    public void clear() {
        if (this.values.getRowCount() > 0) {
            this.values.clear();
            this.fireDatasetChanged();
        }
    }

    public TimePeriod getTimePeriod(int item) {
        return (TimePeriod)this.values.getRowKey(item);
    }

    public int getItemCount() {
        return this.values.getRowCount();
    }

    public int getItemCount(int series) {
        return this.getItemCount();
    }

    public int getSeriesCount() {
        return this.values.getColumnCount();
    }

    public Comparable getSeriesKey(int series) {
        return this.values.getColumnKey(series);
    }

    public Number getX(int series, int item) {
        return new Double(this.getXValue(series, item));
    }

    public double getXValue(int series, int item) {
        TimePeriod period = (TimePeriod)this.values.getRowKey(item);
        return this.getXValue(period);
    }

    public Number getStartX(int series, int item) {
        return new Double(this.getStartXValue(series, item));
    }

    public double getStartXValue(int series, int item) {
        TimePeriod period = (TimePeriod)this.values.getRowKey(item);
        return period.getStart().getTime();
    }

    public Number getEndX(int series, int item) {
        return new Double(this.getEndXValue(series, item));
    }

    public double getEndXValue(int series, int item) {
        TimePeriod period = (TimePeriod)this.values.getRowKey(item);
        return period.getEnd().getTime();
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

    private long getXValue(TimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getStart().getTime();
        } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            long t0 = period.getStart().getTime();
            long t1 = period.getEnd().getTime();
            result = t0 + (t1 - t0) / 2L;
        } else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getEnd().getTime();
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
        List keys = this.values.getRowKeys();
        if (keys.isEmpty()) {
            return null;
        }
        TimePeriod first = (TimePeriod)keys.get(0);
        TimePeriod last = (TimePeriod)keys.get(keys.size() - 1);
        if (!includeInterval || this.domainIsPointsInTime) {
            return new Range(this.getXValue(first), this.getXValue(last));
        }
        return new Range(first.getStart().getTime(), last.getEnd().getTime());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeTableXYDataset)) {
            return false;
        }
        TimeTableXYDataset that = (TimeTableXYDataset)obj;
        if (this.domainIsPointsInTime != that.domainIsPointsInTime) {
            return false;
        }
        if (this.xPosition != that.xPosition) {
            return false;
        }
        if (!this.workingCalendar.getTimeZone().equals(that.workingCalendar.getTimeZone())) {
            return false;
        }
        return this.values.equals(that.values);
    }

    public Object clone() throws CloneNotSupportedException {
        TimeTableXYDataset clone = (TimeTableXYDataset)super.clone();
        clone.values = (DefaultKeyedValues2D)this.values.clone();
        clone.workingCalendar = (Calendar)this.workingCalendar.clone();
        return clone;
    }
}

