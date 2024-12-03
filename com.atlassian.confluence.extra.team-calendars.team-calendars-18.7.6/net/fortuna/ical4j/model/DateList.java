/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Iso8601;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.parameter.Value;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DateList
implements List<Date>,
Serializable,
Iterable<Date> {
    private static final long serialVersionUID = -3700862452550012357L;
    private final Value type;
    private final List<Date> dates;
    private TimeZone timeZone;
    private boolean utc;

    public DateList() {
        this(false);
    }

    public DateList(boolean unmodifiable) {
        this.type = Value.DATE_TIME;
        this.dates = unmodifiable ? Collections.emptyList() : new ArrayList<Date>();
    }

    public DateList(Value aType) {
        this(aType, null);
    }

    public DateList(Value aType, TimeZone timezone) {
        this.type = aType != null ? aType : Value.DATE_TIME;
        this.timeZone = timezone;
        this.dates = new ArrayList<Date>();
    }

    public DateList(String aValue, Value aType) throws ParseException {
        this(aValue, aType, null);
    }

    public DateList(String aValue, Value aType, TimeZone timezone) throws ParseException {
        this(aType, timezone);
        StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {
            if (Value.DATE.equals(this.type)) {
                this.add(new Date(t.nextToken()));
                continue;
            }
            this.add(new DateTime(t.nextToken(), timezone));
        }
    }

    public DateList(DateList list, Value type) {
        if (!Value.DATE.equals(type) && !Value.DATE_TIME.equals(type)) {
            throw new IllegalArgumentException("Type must be either DATE or DATE-TIME");
        }
        this.type = type;
        this.dates = new ArrayList<Date>();
        if (Value.DATE.equals(type)) {
            for (Date date : list) {
                this.add(new Date(date));
            }
        } else {
            for (Date dateTime : list) {
                this.add(new DateTime(dateTime));
            }
        }
    }

    public final String toString() {
        return this.stream().map(Iso8601::toString).collect(Collectors.joining(","));
    }

    @Override
    public final boolean add(Date date) {
        DateTime dateTime;
        if (!this.isUtc() && this.getTimeZone() == null && date instanceof DateTime) {
            dateTime = (DateTime)date;
            if (dateTime.isUtc()) {
                this.setUtc(true);
            } else {
                this.setTimeZone(dateTime.getTimeZone());
            }
        }
        if (date instanceof DateTime) {
            dateTime = (DateTime)date;
            if (this.isUtc()) {
                dateTime.setUtc(true);
            } else {
                dateTime.setTimeZone(this.getTimeZone());
            }
        } else if (!Value.DATE.equals(this.getType())) {
            dateTime = new DateTime(date);
            dateTime.setTimeZone(this.getTimeZone());
            return this.dates.add(dateTime);
        }
        return this.dates.add(date);
    }

    public final boolean remove(Date date) {
        return this.remove((Object)date);
    }

    public final Value getType() {
        return this.type;
    }

    public final boolean isUtc() {
        return this.utc;
    }

    public final void setUtc(boolean utc) {
        if (!Value.DATE.equals(this.type)) {
            for (Date date : this) {
                ((DateTime)date).setUtc(utc);
            }
        }
        this.timeZone = null;
        this.utc = utc;
    }

    public final void setTimeZone(TimeZone timeZone) {
        if (!Value.DATE.equals(this.type)) {
            for (Date date : this) {
                ((DateTime)date).setTimeZone(timeZone);
            }
        }
        this.timeZone = timeZone;
        this.utc = false;
    }

    public final TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override
    public final void add(int arg0, Date arg1) {
        this.dates.add(arg0, arg1);
    }

    @Override
    public final boolean addAll(Collection<? extends Date> arg0) {
        return this.dates.addAll(arg0);
    }

    @Override
    public final boolean addAll(int arg0, Collection<? extends Date> arg1) {
        return this.dates.addAll(arg0, arg1);
    }

    @Override
    public final void clear() {
        this.dates.clear();
    }

    @Override
    public final boolean contains(Object o) {
        return this.dates.contains(o);
    }

    @Override
    public final boolean containsAll(Collection<?> arg0) {
        return this.dates.containsAll(arg0);
    }

    @Override
    public final Date get(int index) {
        return this.dates.get(index);
    }

    @Override
    public final int indexOf(Object o) {
        return this.dates.indexOf(o);
    }

    @Override
    public final boolean isEmpty() {
        return this.dates.isEmpty();
    }

    @Override
    public final Iterator<Date> iterator() {
        return this.dates.iterator();
    }

    @Override
    public final int lastIndexOf(Object o) {
        return this.dates.lastIndexOf(o);
    }

    @Override
    public final ListIterator<Date> listIterator() {
        return this.dates.listIterator();
    }

    @Override
    public final ListIterator<Date> listIterator(int index) {
        return this.dates.listIterator(index);
    }

    @Override
    public final Date remove(int index) {
        return this.dates.remove(index);
    }

    @Override
    public final boolean remove(Object o) {
        return this.dates.remove(o);
    }

    @Override
    public final boolean removeAll(Collection<?> arg0) {
        return this.dates.removeAll(arg0);
    }

    @Override
    public final boolean retainAll(Collection<?> arg0) {
        return this.dates.retainAll(arg0);
    }

    @Override
    public final Date set(int arg0, Date arg1) {
        return this.dates.set(arg0, arg1);
    }

    @Override
    public final int size() {
        return this.dates.size();
    }

    @Override
    public final List<Date> subList(int fromIndex, int toIndex) {
        return this.dates.subList(fromIndex, toIndex);
    }

    @Override
    public final Object[] toArray() {
        return this.dates.toArray();
    }

    @Override
    public final <T> T[] toArray(T[] arg0) {
        return this.dates.toArray(arg0);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!this.getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        DateList rhs = (DateList)obj;
        return new EqualsBuilder().append(this.dates, rhs.dates).append((Object)this.type, (Object)rhs.type).append((Object)this.timeZone, (Object)rhs.timeZone).append(this.utc, this.utc).isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder().append(this.dates).append((Object)this.type).append((Object)this.timeZone).append(this.utc).toHashCode();
    }
}

