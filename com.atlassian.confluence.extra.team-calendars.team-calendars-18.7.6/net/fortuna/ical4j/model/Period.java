/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.text.ParseException;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import net.fortuna.ical4j.model.TemporalAmountComparator;
import net.fortuna.ical4j.model.TimeZone;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Period
extends DateRange
implements Comparable<Period> {
    private static final long serialVersionUID = 7321090422911676490L;
    private TemporalAmountAdapter duration;
    private Component component;

    public Period(String aValue) throws ParseException {
        super(Period.parseStartDate(aValue), Period.parseEndDate(aValue, true));
        try {
            Period.parseEndDate(aValue, false);
        }
        catch (ParseException pe) {
            this.duration = Period.parseDuration(aValue);
        }
        this.normalise();
    }

    public Period(DateTime start, DateTime end) {
        super(start, end);
        this.normalise();
    }

    @Deprecated
    public Period(DateTime start, Dur duration) {
        this(start, TemporalAmountAdapter.from(duration).getDuration());
    }

    public Period(DateTime start, TemporalAmount duration) {
        super(start, new DateTime(Date.from(start.toInstant().plus(duration))));
        this.duration = new TemporalAmountAdapter(duration);
        this.normalise();
    }

    private static DateTime parseStartDate(String value) throws ParseException {
        return new DateTime(value.substring(0, value.indexOf(47)));
    }

    private static DateTime parseEndDate(String value, boolean resolve) throws ParseException {
        DateTime end;
        try {
            end = new DateTime(value.substring(value.indexOf(47) + 1));
        }
        catch (ParseException e) {
            if (resolve) {
                TemporalAmount duration = Period.parseDuration(value).getDuration();
                end = new DateTime(Date.from(Period.parseStartDate(value).toInstant().plus(duration)));
            }
            throw e;
        }
        return end;
    }

    private static TemporalAmountAdapter parseDuration(String value) {
        String durationString = value.substring(value.indexOf(47) + 1);
        return TemporalAmountAdapter.parse(durationString);
    }

    private void normalise() {
        if (this.getStart().isUtc()) {
            this.getEnd().setUtc(true);
        } else {
            this.getEnd().setTimeZone(this.getStart().getTimeZone());
        }
    }

    public final TemporalAmount getDuration() {
        if (this.duration == null) {
            return TemporalAmountAdapter.fromDateRange(this.getStart(), this.getEnd()).getDuration();
        }
        return this.duration.getDuration();
    }

    public final DateTime getEnd() {
        return (DateTime)this.getRangeEnd();
    }

    public final DateTime getStart() {
        return (DateTime)this.getRangeStart();
    }

    @Deprecated
    public final boolean includes(Date date, boolean inclusive) {
        if (inclusive) {
            return this.includes(date, 3);
        }
        return this.includes(date, 0);
    }

    public final Period add(Period period) {
        DateTime newPeriodEnd;
        DateTime newPeriodStart;
        if (period == null) {
            newPeriodStart = this.getStart();
            newPeriodEnd = this.getEnd();
        } else {
            newPeriodStart = this.getStart().before(period.getStart()) ? this.getStart() : period.getStart();
            newPeriodEnd = this.getEnd().after(period.getEnd()) ? this.getEnd() : period.getEnd();
        }
        return new Period(newPeriodStart, newPeriodEnd);
    }

    public final PeriodList subtract(Period period) {
        DateTime newPeriodEnd;
        DateTime newPeriodStart;
        PeriodList result = new PeriodList();
        if (period.contains(this)) {
            return result;
        }
        if (!period.intersects(this)) {
            result.add(this);
            return result;
        }
        if (!period.getStart().after(this.getStart())) {
            newPeriodStart = period.getEnd();
            newPeriodEnd = this.getEnd();
        } else if (!period.getEnd().before(this.getEnd())) {
            newPeriodStart = this.getStart();
            newPeriodEnd = period.getStart();
        } else {
            newPeriodStart = this.getStart();
            newPeriodEnd = period.getStart();
            result.add(new Period(newPeriodStart, newPeriodEnd));
            newPeriodStart = period.getEnd();
            newPeriodEnd = this.getEnd();
        }
        result.add(new Period(newPeriodStart, newPeriodEnd));
        return result;
    }

    public final boolean isEmpty() {
        return this.getStart().equals(this.getEnd());
    }

    public void setUtc(boolean utc) {
        this.getStart().setUtc(utc);
        this.getEnd().setUtc(utc);
    }

    public final void setTimeZone(TimeZone timezone) {
        this.getStart().setUtc(false);
        this.getStart().setTimeZone(timezone);
        this.getEnd().setUtc(false);
        this.getEnd().setTimeZone(timezone);
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        b.append(this.getStart());
        b.append('/');
        if (this.duration == null) {
            b.append(this.getEnd());
        } else {
            b.append(this.duration);
        }
        return b.toString();
    }

    @Override
    public final int compareTo(Period arg0) {
        int endCompare;
        if (arg0 == null) {
            throw new ClassCastException("Cannot compare this object to null");
        }
        int startCompare = this.getStart().compareTo(arg0.getStart());
        if (startCompare != 0) {
            return startCompare;
        }
        if (this.duration == null && (endCompare = this.getEnd().compareTo(arg0.getEnd())) != 0) {
            return endCompare;
        }
        return new TemporalAmountComparator().compare(this.getDuration(), arg0.getDuration());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Period)) {
            return false;
        }
        Period period = (Period)o;
        return new EqualsBuilder().append((Object)this.getStart(), (Object)period.getStart()).append((Object)(this.duration == null ? this.getEnd() : this.duration), (Object)(period.duration == null ? period.getEnd() : period.duration)).isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder().append((Object)this.getStart()).append((Object)(this.duration == null ? this.getEnd() : this.duration)).toHashCode();
    }

    public Component getComponent() {
        return this.component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }
}

