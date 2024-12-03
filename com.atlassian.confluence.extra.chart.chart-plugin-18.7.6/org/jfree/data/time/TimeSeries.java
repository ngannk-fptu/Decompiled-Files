/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.util.ObjectUtilities;

public class TimeSeries
extends Series
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -5032960206869675528L;
    protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";
    protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";
    private String domain;
    private String range;
    protected Class timePeriodClass;
    protected List data;
    private int maximumItemCount;
    private long maximumItemAge;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$util$TimeZone;
    static /* synthetic */ Class class$org$jfree$data$time$RegularTimePeriod;

    public TimeSeries(Comparable name) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION);
    }

    public TimeSeries(Comparable name, String domain, String range) {
        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = null;
        this.data = new ArrayList();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.maximumItemAge = Long.MAX_VALUE;
    }

    public String getDomainDescription() {
        return this.domain;
    }

    public void setDomainDescription(String description) {
        String old = this.domain;
        this.domain = description;
        this.firePropertyChange("Domain", old, description);
    }

    public String getRangeDescription() {
        return this.range;
    }

    public void setRangeDescription(String description) {
        String old = this.range;
        this.range = description;
        this.firePropertyChange("Range", old, description);
    }

    public int getItemCount() {
        return this.data.size();
    }

    public List getItems() {
        return Collections.unmodifiableList(this.data);
    }

    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    public void setMaximumItemCount(int maximum) {
        if (maximum < 0) {
            throw new IllegalArgumentException("Negative 'maximum' argument.");
        }
        this.maximumItemCount = maximum;
        int count = this.data.size();
        if (count > maximum) {
            this.delete(0, count - maximum - 1);
        }
    }

    public long getMaximumItemAge() {
        return this.maximumItemAge;
    }

    public void setMaximumItemAge(long periods) {
        if (periods < 0L) {
            throw new IllegalArgumentException("Negative 'periods' argument.");
        }
        this.maximumItemAge = periods;
        this.removeAgedItems(true);
    }

    public Class getTimePeriodClass() {
        return this.timePeriodClass;
    }

    public TimeSeriesDataItem getDataItem(int index) {
        return (TimeSeriesDataItem)this.data.get(index);
    }

    public TimeSeriesDataItem getDataItem(RegularTimePeriod period) {
        int index = this.getIndex(period);
        if (index >= 0) {
            return (TimeSeriesDataItem)this.data.get(index);
        }
        return null;
    }

    public RegularTimePeriod getTimePeriod(int index) {
        return this.getDataItem(index).getPeriod();
    }

    public RegularTimePeriod getNextTimePeriod() {
        RegularTimePeriod last = this.getTimePeriod(this.getItemCount() - 1);
        return last.next();
    }

    public Collection getTimePeriods() {
        ArrayList<RegularTimePeriod> result = new ArrayList<RegularTimePeriod>();
        for (int i = 0; i < this.getItemCount(); ++i) {
            result.add(this.getTimePeriod(i));
        }
        return result;
    }

    public Collection getTimePeriodsUniqueToOtherSeries(TimeSeries series) {
        ArrayList<RegularTimePeriod> result = new ArrayList<RegularTimePeriod>();
        for (int i = 0; i < series.getItemCount(); ++i) {
            RegularTimePeriod period = series.getTimePeriod(i);
            int index = this.getIndex(period);
            if (index >= 0) continue;
            result.add(period);
        }
        return result;
    }

    public int getIndex(RegularTimePeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, -2.147483648E9);
        return Collections.binarySearch(this.data, dummy);
    }

    public Number getValue(int index) {
        return this.getDataItem(index).getValue();
    }

    public Number getValue(RegularTimePeriod period) {
        int index = this.getIndex(period);
        if (index >= 0) {
            return this.getValue(index);
        }
        return null;
    }

    public void add(TimeSeriesDataItem item) {
        this.add(item, true);
    }

    public void add(TimeSeriesDataItem item, boolean notify) {
        if (item == null) {
            throw new IllegalArgumentException("Null 'item' argument.");
        }
        Class<?> c = item.getPeriod().getClass();
        if (this.timePeriodClass == null) {
            this.timePeriodClass = c;
        } else if (!this.timePeriodClass.equals(c)) {
            StringBuffer b = new StringBuffer();
            b.append("You are trying to add data where the time period class ");
            b.append("is ");
            b.append(item.getPeriod().getClass().getName());
            b.append(", but the TimeSeries is expecting an instance of ");
            b.append(this.timePeriodClass.getName());
            b.append(".");
            throw new SeriesException(b.toString());
        }
        boolean added = false;
        int count = this.getItemCount();
        if (count == 0) {
            this.data.add(item);
            added = true;
        } else {
            RegularTimePeriod last = this.getTimePeriod(this.getItemCount() - 1);
            if (item.getPeriod().compareTo(last) > 0) {
                this.data.add(item);
                added = true;
            } else {
                int index = Collections.binarySearch(this.data, item);
                if (index < 0) {
                    this.data.add(-index - 1, item);
                    added = true;
                } else {
                    StringBuffer b = new StringBuffer();
                    b.append("You are attempting to add an observation for ");
                    b.append("the time period ");
                    b.append(item.getPeriod().toString());
                    b.append(" but the series already contains an observation");
                    b.append(" for that time period. Duplicates are not ");
                    b.append("permitted.  Try using the addOrUpdate() method.");
                    throw new SeriesException(b.toString());
                }
            }
        }
        if (added) {
            if (this.getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }
            this.removeAgedItems(false);
            if (notify) {
                this.fireSeriesChanged();
            }
        }
    }

    public void add(RegularTimePeriod period, double value) {
        this.add(period, value, true);
    }

    public void add(RegularTimePeriod period, double value, boolean notify) {
        TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        this.add(item, notify);
    }

    public void add(RegularTimePeriod period, Number value) {
        this.add(period, value, true);
    }

    public void add(RegularTimePeriod period, Number value, boolean notify) {
        TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        this.add(item, notify);
    }

    public void update(RegularTimePeriod period, Number value) {
        TimeSeriesDataItem temp = new TimeSeriesDataItem(period, value);
        int index = Collections.binarySearch(this.data, temp);
        if (index < 0) {
            throw new SeriesException("There is no existing value for the specified 'period'.");
        }
        TimeSeriesDataItem pair = (TimeSeriesDataItem)this.data.get(index);
        pair.setValue(value);
        this.fireSeriesChanged();
    }

    public void update(int index, Number value) {
        TimeSeriesDataItem item = this.getDataItem(index);
        item.setValue(value);
        this.fireSeriesChanged();
    }

    public TimeSeries addAndOrUpdate(TimeSeries series) {
        TimeSeries overwritten = new TimeSeries((Comparable)((Object)("Overwritten values from: " + this.getKey())));
        for (int i = 0; i < series.getItemCount(); ++i) {
            TimeSeriesDataItem item = series.getDataItem(i);
            TimeSeriesDataItem oldItem = this.addOrUpdate(item.getPeriod(), item.getValue());
            if (oldItem == null) continue;
            overwritten.add(oldItem);
        }
        return overwritten;
    }

    public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period, double value) {
        return this.addOrUpdate(period, new Double(value));
    }

    public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period, Number value) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        TimeSeriesDataItem overwritten = null;
        TimeSeriesDataItem key = new TimeSeriesDataItem(period, value);
        int index = Collections.binarySearch(this.data, key);
        if (index >= 0) {
            TimeSeriesDataItem existing = (TimeSeriesDataItem)this.data.get(index);
            overwritten = (TimeSeriesDataItem)existing.clone();
            existing.setValue(value);
            this.removeAgedItems(false);
            this.fireSeriesChanged();
        } else {
            this.data.add(-index - 1, new TimeSeriesDataItem(period, value));
            this.timePeriodClass = period.getClass();
            if (this.getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
                if (this.data.isEmpty()) {
                    this.timePeriodClass = null;
                }
            }
            this.removeAgedItems(false);
            this.fireSeriesChanged();
        }
        return overwritten;
    }

    public void removeAgedItems(boolean notify) {
        if (this.getItemCount() > 1) {
            long latest = this.getTimePeriod(this.getItemCount() - 1).getSerialIndex();
            boolean removed = false;
            while (latest - this.getTimePeriod(0).getSerialIndex() > this.maximumItemAge) {
                this.data.remove(0);
                removed = true;
            }
            if (removed && notify) {
                this.fireSeriesChanged();
            }
        }
    }

    public void removeAgedItems(long latest, boolean notify) {
        if (this.data.isEmpty()) {
            return;
        }
        long index = Long.MAX_VALUE;
        try {
            Method m = (class$org$jfree$data$time$RegularTimePeriod == null ? (class$org$jfree$data$time$RegularTimePeriod = TimeSeries.class$("org.jfree.data.time.RegularTimePeriod")) : class$org$jfree$data$time$RegularTimePeriod).getDeclaredMethod("createInstance", class$java$lang$Class == null ? (class$java$lang$Class = TimeSeries.class$("java.lang.Class")) : class$java$lang$Class, class$java$util$Date == null ? (class$java$util$Date = TimeSeries.class$("java.util.Date")) : class$java$util$Date, class$java$util$TimeZone == null ? (class$java$util$TimeZone = TimeSeries.class$("java.util.TimeZone")) : class$java$util$TimeZone);
            RegularTimePeriod newest = (RegularTimePeriod)m.invoke((Object)this.timePeriodClass, this.timePeriodClass, new Date(latest), TimeZone.getDefault());
            index = newest.getSerialIndex();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        boolean removed = false;
        while (this.getItemCount() > 0 && index - this.getTimePeriod(0).getSerialIndex() > this.maximumItemAge) {
            this.data.remove(0);
            removed = true;
        }
        if (removed && notify) {
            this.fireSeriesChanged();
        }
    }

    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            this.timePeriodClass = null;
            this.fireSeriesChanged();
        }
    }

    public void delete(RegularTimePeriod period) {
        int index = this.getIndex(period);
        if (index >= 0) {
            this.data.remove(index);
            if (this.data.isEmpty()) {
                this.timePeriodClass = null;
            }
            this.fireSeriesChanged();
        }
    }

    public void delete(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        for (int i = 0; i <= end - start; ++i) {
            this.data.remove(start);
        }
        if (this.data.isEmpty()) {
            this.timePeriodClass = null;
        }
        this.fireSeriesChanged();
    }

    public Object clone() throws CloneNotSupportedException {
        TimeSeries clone = (TimeSeries)super.clone();
        clone.data = (List)ObjectUtilities.deepClone(this.data);
        return clone;
    }

    public TimeSeries createCopy(int start, int end) throws CloneNotSupportedException {
        if (start < 0) {
            throw new IllegalArgumentException("Requires start >= 0.");
        }
        if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        TimeSeries copy = (TimeSeries)super.clone();
        copy.data = new ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; ++index) {
                TimeSeriesDataItem item = (TimeSeriesDataItem)this.data.get(index);
                TimeSeriesDataItem clone = (TimeSeriesDataItem)item.clone();
                try {
                    copy.add(clone);
                    continue;
                }
                catch (SeriesException e) {
                    e.printStackTrace();
                }
            }
        }
        return copy;
    }

    public TimeSeries createCopy(RegularTimePeriod start, RegularTimePeriod end) throws CloneNotSupportedException {
        int endIndex;
        if (start == null) {
            throw new IllegalArgumentException("Null 'start' argument.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Null 'end' argument.");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Requires start on or before end.");
        }
        boolean emptyRange = false;
        int startIndex = this.getIndex(start);
        if (startIndex < 0 && (startIndex = -(startIndex + 1)) == this.data.size()) {
            emptyRange = true;
        }
        if ((endIndex = this.getIndex(end)) < 0) {
            endIndex = -(endIndex + 1);
            --endIndex;
        }
        if (endIndex < 0 || endIndex < startIndex) {
            emptyRange = true;
        }
        if (emptyRange) {
            TimeSeries copy = (TimeSeries)super.clone();
            copy.data = new ArrayList();
            return copy;
        }
        return this.createCopy(startIndex, endIndex);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TimeSeries)) {
            return false;
        }
        TimeSeries that = (TimeSeries)object;
        if (!ObjectUtilities.equal(this.getDomainDescription(), that.getDomainDescription())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.getRangeDescription(), that.getRangeDescription())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.timePeriodClass, that.timePeriodClass)) {
            return false;
        }
        if (this.getMaximumItemAge() != that.getMaximumItemAge()) {
            return false;
        }
        if (this.getMaximumItemCount() != that.getMaximumItemCount()) {
            return false;
        }
        int count = this.getItemCount();
        if (count != that.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; ++i) {
            if (this.getDataItem(i).equals(that.getDataItem(i))) continue;
            return false;
        }
        return super.equals(object);
    }

    public int hashCode() {
        TimeSeriesDataItem item;
        int result = super.hashCode();
        result = 29 * result + (this.domain != null ? this.domain.hashCode() : 0);
        result = 29 * result + (this.range != null ? this.range.hashCode() : 0);
        result = 29 * result + (this.timePeriodClass != null ? this.timePeriodClass.hashCode() : 0);
        int count = this.getItemCount();
        if (count > 0) {
            item = this.getDataItem(0);
            result = 29 * result + item.hashCode();
        }
        if (count > 1) {
            item = this.getDataItem(count - 1);
            result = 29 * result + item.hashCode();
        }
        if (count > 2) {
            item = this.getDataItem(count / 2);
            result = 29 * result + item.hashCode();
        }
        result = 29 * result + this.maximumItemCount;
        result = 29 * result + (int)this.maximumItemAge;
        return result;
    }

    public TimeSeries(Comparable name, Class timePeriodClass) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION, timePeriodClass);
    }

    public TimeSeries(Comparable name, String domain, String range, Class timePeriodClass) {
        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = timePeriodClass;
        this.data = new ArrayList();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.maximumItemAge = Long.MAX_VALUE;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

