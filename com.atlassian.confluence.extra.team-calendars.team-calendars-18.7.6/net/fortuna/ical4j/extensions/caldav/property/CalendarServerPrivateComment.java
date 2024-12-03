/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class CalendarServerPrivateComment
extends Property {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "X-CALENDARSERVER-PRIVATE-COMMENT";
    private String value;

    public CalendarServerPrivateComment() {
        super(PROPERTY_NAME, new Factory());
    }

    public CalendarServerPrivateComment(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList, new Factory());
        this.setValue(value);
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
    implements PropertyFactory<CalendarServerPrivateComment> {
        private static final long serialVersionUID = 2099427445505899578L;

        public Factory() {
            super(CalendarServerPrivateComment.PROPERTY_NAME);
        }

        @Override
        public CalendarServerPrivateComment createProperty() {
            return new CalendarServerPrivateComment();
        }

        @Override
        public CalendarServerPrivateComment createProperty(ParameterList parameters, String value) {
            CalendarServerPrivateComment property = null;
            property = new CalendarServerPrivateComment(parameters, value);
            return property;
        }
    }
}

