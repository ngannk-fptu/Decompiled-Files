/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DateRange
implements Serializable {
    private static final long serialVersionUID = -7303846680559287286L;
    public static final int INCLUSIVE_START = 1;
    public static final int INCLUSIVE_END = 2;
    private final Date rangeStart;
    private final Date rangeEnd;

    public DateRange(Date start, Date end) {
        if (start == null) {
            throw new IllegalArgumentException("Range start is null");
        }
        if (end == null) {
            throw new IllegalArgumentException("Range end is null");
        }
        if (end.before(start)) {
            throw new IllegalArgumentException("Range start must be before range end");
        }
        this.rangeStart = start;
        this.rangeEnd = end;
    }

    public Date getRangeStart() {
        return this.rangeStart;
    }

    public Date getRangeEnd() {
        return this.rangeEnd;
    }

    public final boolean includes(Date date) {
        return this.includes(date, 3);
    }

    public final boolean includes(Date date, int inclusiveMask) {
        boolean includes = (inclusiveMask & 1) > 0 ? !this.rangeStart.after(date) : this.rangeStart.before(date);
        includes = (inclusiveMask & 2) > 0 ? includes && !this.rangeEnd.before(date) : includes && this.rangeEnd.after(date);
        return includes;
    }

    public final boolean before(DateRange range) {
        return this.rangeEnd.before(range.getRangeStart());
    }

    public final boolean after(DateRange range) {
        return this.rangeStart.after(range.getRangeEnd());
    }

    public final boolean intersects(DateRange range) {
        boolean intersects = false;
        if (range.includes(this.rangeStart) && !range.getRangeEnd().equals(this.rangeStart)) {
            intersects = true;
        } else if (this.includes(range.getRangeStart()) && !this.rangeEnd.equals(range.getRangeStart())) {
            intersects = true;
        }
        return intersects;
    }

    public final boolean adjacent(DateRange range) {
        boolean adjacent = false;
        if (this.rangeStart.equals(range.getRangeEnd())) {
            adjacent = true;
        } else if (this.rangeEnd.equals(range.getRangeStart())) {
            adjacent = true;
        }
        return adjacent;
    }

    public final boolean contains(DateRange range) {
        return this.includes(range.getRangeStart()) && this.includes(range.getRangeEnd());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DateRange dateRange = (DateRange)o;
        return new EqualsBuilder().append((Object)this.rangeStart, (Object)dateRange.rangeStart).append((Object)this.rangeEnd, (Object)dateRange.rangeEnd).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.rangeStart).append((Object)this.rangeEnd).toHashCode();
    }
}

