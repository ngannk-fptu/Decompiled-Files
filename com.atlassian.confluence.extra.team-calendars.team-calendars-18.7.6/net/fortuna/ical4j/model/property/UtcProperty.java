/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.validate.ValidationException;

public abstract class UtcProperty
extends DateProperty {
    private static final long serialVersionUID = 4850079486497487938L;

    public UtcProperty(String name, ParameterList parameters, PropertyFactory factory) {
        super(name, parameters, factory);
        this.setDate(new DateTime(true));
    }

    public UtcProperty(String name, PropertyFactory factory) {
        super(name, factory);
        this.setDate(new DateTime(true));
    }

    public final DateTime getDateTime() {
        return (DateTime)this.getDate();
    }

    public void setDateTime(DateTime dateTime) {
        if (dateTime != null) {
            DateTime utcDateTime = new DateTime(dateTime);
            utcDateTime.setUtc(true);
            this.setDate(utcDateTime);
        } else {
            this.setDate(null);
        }
    }

    @Override
    public void setTimeZone(TimeZone timezone) {
        throw new UnsupportedOperationException("Cannot set timezone for UTC properties");
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (this.getDate() != null && !(this.getDate() instanceof DateTime)) {
            throw new ValidationException("Property must have a DATE-TIME value");
        }
        DateTime dateTime = (DateTime)this.getDate();
        if (dateTime != null && !dateTime.isUtc()) {
            throw new ValidationException(this.getName() + ": DATE-TIME value must be specified in UTC time");
        }
    }
}

