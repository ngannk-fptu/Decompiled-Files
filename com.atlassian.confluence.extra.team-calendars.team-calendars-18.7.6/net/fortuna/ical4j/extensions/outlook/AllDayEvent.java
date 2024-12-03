/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.outlook;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class AllDayEvent
extends Property {
    private static final long serialVersionUID = -3514682572599864426L;
    public static final String PROPERTY_NAME = "X-MICROSOFT-CDO-ALLDAYEVENT";
    public static final AllDayEvent FALSE = new AllDayEvent(new ParameterList(true), "FALSE");
    private String value;

    public AllDayEvent() {
        super(PROPERTY_NAME, new Factory());
    }

    public AllDayEvent(ParameterList aList, String value) {
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
    implements PropertyFactory<AllDayEvent> {
        private static final long serialVersionUID = 596282786680252116L;

        public Factory() {
            super(AllDayEvent.PROPERTY_NAME);
        }

        @Override
        public AllDayEvent createProperty() {
            return new AllDayEvent();
        }

        @Override
        public AllDayEvent createProperty(ParameterList parameters, String value) {
            AllDayEvent property = null;
            property = FALSE.getValue().equals(value) ? FALSE : new AllDayEvent(parameters, value);
            return property;
        }
    }
}

