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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.TimeZone;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PeriodList
implements Set<Period>,
Serializable {
    private static final long serialVersionUID = -2317587285790834492L;
    private final Set<Period> periods;
    private TimeZone timezone;
    private boolean utc;
    private final boolean unmodifiable;

    public PeriodList() {
        this(true);
    }

    public PeriodList(boolean utc) {
        this(utc, false);
    }

    public PeriodList(boolean utc, boolean unmodifiable) {
        this.utc = utc;
        this.unmodifiable = unmodifiable;
        this.periods = unmodifiable ? Collections.emptySet() : new TreeSet<Period>();
    }

    public PeriodList(String aValue) throws ParseException {
        this();
        StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {
            this.add(new Period(t.nextToken()));
        }
    }

    public final String toString() {
        return this.stream().map(Period::toString).collect(Collectors.joining(","));
    }

    @Override
    public final boolean add(Period period) {
        if (this.isUtc()) {
            period.setUtc(true);
        } else {
            period.setTimeZone(this.timezone);
        }
        return this.periods.add(period);
    }

    @Override
    public final boolean remove(Object period) {
        return this.periods.remove(period);
    }

    public final PeriodList normalise() {
        Period prevPeriod = null;
        PeriodList newList = new PeriodList(this.isUtc());
        if (this.timezone != null) {
            newList.setTimeZone(this.timezone);
        }
        boolean normalised = false;
        for (Period period1 : this) {
            Period period = period1;
            if (period.isEmpty()) {
                period = prevPeriod;
                normalised = true;
            } else if (prevPeriod != null) {
                if (prevPeriod.contains(period)) {
                    period = prevPeriod;
                    normalised = true;
                } else if (prevPeriod.intersects(period)) {
                    period = prevPeriod.add(period);
                    normalised = true;
                } else if (prevPeriod.adjacent(period)) {
                    period = prevPeriod.add(period);
                    normalised = true;
                } else {
                    newList.add(prevPeriod);
                }
            }
            prevPeriod = period;
        }
        if (prevPeriod != null) {
            newList.add(prevPeriod);
        }
        if (normalised) {
            return newList;
        }
        return this;
    }

    public final PeriodList add(PeriodList periods) {
        if (periods != null) {
            PeriodList newList = new PeriodList();
            newList.addAll(this);
            newList.addAll(periods);
            return newList.normalise();
        }
        return this;
    }

    public final PeriodList subtract(PeriodList subtractions) {
        if (subtractions == null || subtractions.isEmpty()) {
            return this;
        }
        PeriodList result = this;
        PeriodList tmpResult = new PeriodList();
        for (Period subtraction : subtractions) {
            for (Period period : result) {
                tmpResult.addAll(period.subtract(subtraction));
            }
            result = tmpResult;
            tmpResult = new PeriodList();
        }
        return result;
    }

    public final boolean isUtc() {
        return this.utc;
    }

    public boolean isUnmodifiable() {
        return this.unmodifiable;
    }

    public final void setUtc(boolean utc) {
        for (Period period : this) {
            period.setUtc(utc);
        }
        this.timezone = null;
        this.utc = utc;
    }

    public final void setTimeZone(TimeZone timeZone) {
        for (Period period : this) {
            period.setTimeZone(timeZone);
        }
        this.timezone = timeZone;
        this.utc = false;
    }

    public final TimeZone getTimeZone() {
        return this.timezone;
    }

    @Override
    public boolean addAll(Collection<? extends Period> arg0) {
        for (Period period : arg0) {
            this.add(period);
        }
        return true;
    }

    @Override
    public void clear() {
        this.periods.clear();
    }

    @Override
    public boolean contains(Object o) {
        return this.periods.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return this.periods.containsAll(arg0);
    }

    @Override
    public boolean isEmpty() {
        return this.periods.isEmpty();
    }

    @Override
    public Iterator<Period> iterator() {
        return this.periods.iterator();
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        return this.periods.removeAll(arg0);
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return this.periods.retainAll(arg0);
    }

    @Override
    public int size() {
        return this.periods.size();
    }

    @Override
    public Object[] toArray() {
        return this.periods.toArray();
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return this.periods.toArray(arg0);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PeriodList)) {
            return false;
        }
        PeriodList rhs = (PeriodList)obj;
        return new EqualsBuilder().append(this.periods, rhs.periods).append((Object)this.timezone, (Object)rhs.timezone).append(this.utc, rhs.utc).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.periods).append((Object)this.timezone).append(this.utc).toHashCode();
    }
}

