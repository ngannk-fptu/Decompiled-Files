/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Strings;

public abstract class DateListProperty
extends Property {
    private static final long serialVersionUID = 5233773091972759919L;
    private DateList dates;
    private TimeZone timeZone;

    public DateListProperty(String name, PropertyFactory factory) {
        this(name, new DateList(Value.DATE_TIME), factory);
    }

    public DateListProperty(String name, ParameterList parameters, PropertyFactory factory) {
        super(name, parameters, factory);
    }

    public DateListProperty(String name, DateList dates, PropertyFactory factory) {
        this(name, new ParameterList(), dates, factory);
    }

    public DateListProperty(String name, ParameterList parameters, DateList dates, PropertyFactory factory) {
        super(name, parameters, factory);
        this.dates = dates;
        if (dates != null && !Value.DATE_TIME.equals(dates.getType())) {
            this.getParameters().replace(dates.getType());
        }
    }

    public final DateList getDates() {
        return this.dates;
    }

    @Override
    public void setValue(String aValue) throws ParseException {
        this.dates = new DateList(aValue, (Value)this.getParameter("VALUE"), this.timeZone);
    }

    @Override
    public String getValue() {
        return Strings.valueOf(this.dates);
    }

    public void setTimeZone(TimeZone timezone) {
        if (this.dates == null) {
            throw new UnsupportedOperationException("TimeZone is not applicable to current value");
        }
        this.timeZone = timezone;
        if (timezone != null) {
            if (!Value.DATE_TIME.equals(this.getDates().getType())) {
                throw new UnsupportedOperationException("TimeZone is not applicable to current value");
            }
            this.dates.setTimeZone(timezone);
            this.getParameters().remove((Parameter)this.getParameter("TZID"));
            TzId tzId = new TzId(timezone.getID());
            this.getParameters().replace(tzId);
        } else {
            this.setUtc(false);
        }
    }

    public final TimeZone getTimeZone() {
        return this.timeZone;
    }

    public final void setUtc(boolean utc) {
        if (this.dates == null || !Value.DATE_TIME.equals(this.dates.getType())) {
            throw new UnsupportedOperationException("TimeZone is not applicable to current value");
        }
        this.dates.setUtc(utc);
        this.getParameters().remove((Parameter)this.getParameter("TZID"));
    }

    @Override
    public final Property copy() throws IOException, URISyntaxException, ParseException {
        Property copy = super.copy();
        ((DateListProperty)copy).timeZone = this.timeZone;
        ((DateListProperty)copy).setValue(this.getValue());
        return copy;
    }
}

