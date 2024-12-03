/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class CalendarServerAccess
extends Property {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "X-CALENDARSERVER-ACCESS";
    private String value;
    public static final CalendarServerAccess PUBLIC = new ImmutableCalendarServerAccess("PUBLIC");
    public static final CalendarServerAccess PRIVATE = new ImmutableCalendarServerAccess("PRIVATE");
    public static final CalendarServerAccess CONFIDENTIAL = new ImmutableCalendarServerAccess("CONFIDENTIAL");
    public static final CalendarServerAccess RESTRICTED = new ImmutableCalendarServerAccess("RESTRICTED");

    public CalendarServerAccess() {
        super(PROPERTY_NAME, new Factory());
    }

    public CalendarServerAccess(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList, new Factory());
        this.value = value;
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public void validate() throws ValidationException {
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<CalendarServerAccess> {
        private static final long serialVersionUID = 2099427445505899578L;

        public Factory() {
            super(CalendarServerAccess.PROPERTY_NAME);
        }

        @Override
        public CalendarServerAccess createProperty() {
            return new CalendarServerAccess();
        }

        @Override
        public CalendarServerAccess createProperty(ParameterList parameters, String value) {
            CalendarServerAccess property = null;
            property = PUBLIC.getValue().equals(value) ? PUBLIC : new CalendarServerAccess(parameters, value);
            return property;
        }
    }

    private static final class ImmutableCalendarServerAccess
    extends CalendarServerAccess {
        private static final long serialVersionUID = -2054338254L;

        private ImmutableCalendarServerAccess(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

