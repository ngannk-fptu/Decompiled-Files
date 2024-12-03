/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class CalendarServerAttendeeComment
extends Property {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "X-CALENDARSERVER-ATTENDEE-COMMENT";
    private String value;

    public CalendarServerAttendeeComment() {
        super(PROPERTY_NAME, new Factory());
    }

    public CalendarServerAttendeeComment(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList, new Factory());
        this.setValue(value);
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public void validate() throws ValidationException {
        ParameterValidator.assertOne("X-CALENDARSERVER-DTSTAMP", this.getParameters());
        ParameterValidator.assertOne("X-CALENDARSERVER-ATTENDEE-REF", this.getParameters());
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<CalendarServerAttendeeComment> {
        private static final long serialVersionUID = 2099427445505899578L;

        public Factory() {
            super(CalendarServerAttendeeComment.PROPERTY_NAME);
        }

        @Override
        public CalendarServerAttendeeComment createProperty() {
            return new CalendarServerAttendeeComment();
        }

        @Override
        public CalendarServerAttendeeComment createProperty(ParameterList parameters, String value) {
            CalendarServerAttendeeComment property = null;
            property = new CalendarServerAttendeeComment(parameters, value);
            return property;
        }
    }
}

