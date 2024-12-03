/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.ComponentFactoryImpl;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Component
implements Serializable {
    private static final long serialVersionUID = 4943193483665822201L;
    public static final String BEGIN = "BEGIN";
    public static final String END = "END";
    public static final String VEVENT = "VEVENT";
    public static final String VTODO = "VTODO";
    public static final String VJOURNAL = "VJOURNAL";
    public static final String VFREEBUSY = "VFREEBUSY";
    public static final String VTIMEZONE = "VTIMEZONE";
    public static final String VALARM = "VALARM";
    public static final String VAVAILABILITY = "VAVAILABILITY";
    public static final String VVENUE = "VVENUE";
    public static final String AVAILABLE = "AVAILABLE";
    public static final String EXPERIMENTAL_PREFIX = "X-";
    private String name;
    private PropertyList<Property> properties;

    protected Component(String s) {
        this(s, new PropertyList<Property>());
    }

    protected Component(String s, PropertyList<Property> p) {
        this.name = s;
        this.properties = p;
    }

    public String toString() {
        return "BEGIN:" + this.getName() + "\r\n" + this.getProperties() + END + ':' + this.getName() + "\r\n";
    }

    public final String getName() {
        return this.name;
    }

    public final PropertyList<Property> getProperties() {
        return this.properties;
    }

    public final <C extends Property> PropertyList<C> getProperties(String name) {
        return this.getProperties().getProperties(name);
    }

    public final <T extends Property> T getProperty(String name) {
        return (T)((Property)this.getProperties().getProperty(name));
    }

    protected final Property getRequiredProperty(String name) throws ConstraintViolationException {
        Property p = (Property)this.getProperties().getProperty(name);
        if (p == null) {
            throw new ConstraintViolationException(String.format("Missing %s property", name));
        }
        return p;
    }

    public final void validate() throws ValidationException {
        this.validate(true);
    }

    public abstract void validate(boolean var1) throws ValidationException;

    protected final void validateProperties() throws ValidationException {
        for (Property property : this.getProperties()) {
            property.validate();
        }
    }

    public boolean equals(Object arg0) {
        if (arg0 instanceof Component) {
            Component c = (Component)arg0;
            return new EqualsBuilder().append((Object)this.getName(), (Object)c.getName()).append(this.getProperties(), c.getProperties()).isEquals();
        }
        return super.equals(arg0);
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getName()).append(this.getProperties()).toHashCode();
    }

    public Component copy() throws ParseException, IOException, URISyntaxException {
        PropertyList<Property> newprops = new PropertyList<Property>(this.getProperties());
        return new ComponentFactoryImpl().createComponent(this.getName(), newprops);
    }

    public final PeriodList calculateRecurrenceSet(Period period) {
        PeriodList recurrenceSet = new PeriodList();
        DtStart start = (DtStart)this.getProperty("DTSTART");
        DateProperty end = (DateProperty)this.getProperty("DTEND");
        if (end == null) {
            end = (DateProperty)this.getProperty("DUE");
        }
        Duration duration = (Duration)this.getProperty("DURATION");
        if (start == null) {
            return recurrenceSet;
        }
        Value startValue = (Value)start.getParameter("VALUE");
        if (start.isUtc()) {
            recurrenceSet.setUtc(true);
        } else if (start.getDate() instanceof DateTime) {
            recurrenceSet.setTimeZone(((DateTime)start.getDate()).getTimeZone());
        }
        TemporalAmount rDuration = end == null && duration == null ? java.time.Duration.ZERO : (duration == null ? TemporalAmountAdapter.fromDateRange(start.getDate(), end.getDate()).getDuration() : duration.getDuration());
        PropertyList rDates = this.getProperties("RDATE");
        recurrenceSet.addAll(rDates.stream().filter(p -> p.getParameter("VALUE") == Value.PERIOD).map(p -> p.getPeriods()).flatMap(Collection::stream).filter(rdatePeriod -> period.intersects((DateRange)rdatePeriod)).collect(Collectors.toList()));
        recurrenceSet.addAll(rDates.stream().filter(p -> p.getParameter("VALUE") == Value.DATE_TIME).map(p -> p.getDates()).flatMap(Collection::stream).filter(date -> period.includes((java.util.Date)date)).map(rdateTime -> new Period((DateTime)rdateTime, rDuration)).collect(Collectors.toList()));
        recurrenceSet.addAll(rDates.stream().filter(p -> p.getParameter("VALUE") == Value.DATE).map(p -> p.getDates()).flatMap(Collection::stream).filter(date -> period.includes((java.util.Date)date)).map(rdateDate -> new Period(new DateTime((java.util.Date)rdateDate), rDuration)).collect(Collectors.toList()));
        DateTime startMinusDuration = new DateTime(period.getStart());
        startMinusDuration.setTime(Date.from(period.getStart().toInstant().minus(rDuration)).getTime());
        PropertyList rRules = this.getProperties("RRULE");
        if (!rRules.isEmpty()) {
            recurrenceSet.addAll(rRules.stream().map(r -> r.getRecur().getDates(start.getDate(), new Period(startMinusDuration, period.getEnd()), startValue)).flatMap(Collection::stream).map(rruleDate -> new Period(new DateTime((java.util.Date)rruleDate), rDuration)).collect(Collectors.toList()));
        } else {
            Period startPeriod;
            if (end != null) {
                startPeriod = new Period(new DateTime(start.getDate()), new DateTime(end.getDate()));
            } else {
                if (duration == null) {
                    duration = new Duration(rDuration);
                }
                startPeriod = new Period(new DateTime(start.getDate()), duration.getDuration());
            }
            if (period.intersects(startPeriod)) {
                recurrenceSet.add(startPeriod);
            }
        }
        PropertyList exDateProps = this.getProperties("EXDATE");
        List exDates = exDateProps.stream().map(e -> e.getDates()).flatMap(Collection::stream).collect(Collectors.toList());
        recurrenceSet.removeIf(recurrence -> exDates.contains(recurrence.getStart()) || exDates.contains(new Date(recurrence.getStart())));
        PropertyList exRules = this.getProperties("EXRULE");
        List exRuleDates = exRules.stream().map(e -> e.getRecur().getDates(start.getDate(), period, startValue)).flatMap(Collection::stream).collect(Collectors.toList());
        recurrenceSet.removeIf(recurrence -> exRuleDates.contains(recurrence.getStart()) || exRuleDates.contains(new Date(recurrence.getStart())));
        recurrenceSet.forEach(p -> p.setComponent(this));
        return recurrenceSet;
    }
}

