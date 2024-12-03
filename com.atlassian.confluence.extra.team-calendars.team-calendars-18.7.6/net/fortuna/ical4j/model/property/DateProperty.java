/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public abstract class DateProperty
extends Property {
    private static final long serialVersionUID = 3160883132732961321L;
    private Date date;
    private TimeZone timeZone;

    public DateProperty(String name, ParameterList parameters, PropertyFactory factory) {
        super(name, parameters, factory);
    }

    public DateProperty(String name, PropertyFactory factory) {
        super(name, factory);
    }

    public DateProperty(String name, TimeZone timezone, PropertyFactory factory) {
        super(name, factory);
        this.updateTimeZone(timezone);
    }

    public final Date getDate() {
        return this.date;
    }

    public final void setDate(Date date) {
        this.date = date;
        if (date instanceof DateTime) {
            if (Value.DATE.equals(this.getParameter("VALUE"))) {
                this.getParameters().replace(Value.DATE_TIME);
            }
            this.updateTimeZone(((DateTime)date).getTimeZone());
        } else {
            if (date != null) {
                this.getParameters().replace(Value.DATE);
            }
            this.updateTimeZone(null);
        }
    }

    @Override
    public void setValue(String value) throws ParseException {
        if (Value.DATE.equals(this.getParameter("VALUE"))) {
            this.updateTimeZone(null);
            this.date = new Date(value);
        } else if (value != null && !value.isEmpty()) {
            this.date = new DateTime(value, this.timeZone);
        }
    }

    @Override
    public String getValue() {
        return Strings.valueOf(this.getDate());
    }

    public void setTimeZone(TimeZone timezone) {
        this.updateTimeZone(timezone);
    }

    public final TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override
    public int hashCode() {
        return this.getDate() != null ? this.getDate().hashCode() : 0;
    }

    private void updateTimeZone(TimeZone timezone) {
        this.timeZone = timezone;
        if (timezone != null) {
            if (this.getDate() != null && !(this.getDate() instanceof DateTime)) {
                throw new UnsupportedOperationException("TimeZone is not applicable to current value");
            }
            if (this.getDate() != null) {
                ((DateTime)this.getDate()).setTimeZone(timezone);
            }
            this.getParameters().replace(new TzId(timezone.getID()));
        } else {
            this.setUtc(this.isUtc());
        }
    }

    public final void setUtc(boolean utc) {
        if (this.getDate() != null && this.getDate() instanceof DateTime) {
            ((DateTime)this.getDate()).setUtc(utc);
        }
        this.getParameters().remove((Parameter)this.getParameter("TZID"));
    }

    public final boolean isUtc() {
        return this.getDate() instanceof DateTime && ((DateTime)this.getDate()).isUtc();
    }

    @Override
    public void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("VALUE", this.getParameters());
        if (this.isUtc()) {
            ParameterValidator.assertNone("TZID", this.getParameters());
        } else {
            ParameterValidator.assertOneOrLess("TZID", this.getParameters());
        }
        Value value = (Value)this.getParameter("VALUE");
        if (this.getDate() instanceof DateTime) {
            if (value != null && !Value.DATE_TIME.equals(value)) {
                throw new ValidationException("VALUE parameter [" + value + "] is invalid for DATE-TIME instance");
            }
            DateTime dateTime = (DateTime)this.date;
            Object tzId = this.getParameter("TZID");
            if (!(dateTime.getTimeZone() == null || tzId != null && ((Content)tzId).getValue().equals(dateTime.getTimeZone().getID()))) {
                throw new ValidationException("TZID parameter [" + tzId + "] does not match the timezone [" + dateTime.getTimeZone().getID() + "]");
            }
        } else if (this.getDate() != null) {
            if (value == null) {
                throw new ValidationException("VALUE parameter [" + Value.DATE + "] must be specified for DATE instance");
            }
            if (!Value.DATE.equals(value)) {
                throw new ValidationException("VALUE parameter [" + value + "] is invalid for DATE instance");
            }
        }
    }

    @Override
    public Property copy() throws IOException, URISyntaxException, ParseException {
        Property copy = super.copy();
        ((DateProperty)copy).timeZone = this.timeZone;
        ((DateProperty)copy).setValue(this.getValue());
        return copy;
    }
}

