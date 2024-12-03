/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class DefaultAlarm
extends Property {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "DEFAULT-ALARM";
    private String value;
    public static final DefaultAlarm TRUE = new ImmutableDefaultAlarm("TRUE");
    public static final DefaultAlarm FALSE = new ImmutableDefaultAlarm("FALSE");

    public DefaultAlarm() {
        super(PROPERTY_NAME, new Factory());
    }

    public DefaultAlarm(ParameterList aList, String value) {
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
    implements PropertyFactory<DefaultAlarm> {
        private static final long serialVersionUID = 2099427445505899578L;

        public Factory() {
            super(DefaultAlarm.PROPERTY_NAME);
        }

        @Override
        public DefaultAlarm createProperty() {
            return new DefaultAlarm();
        }

        @Override
        public DefaultAlarm createProperty(ParameterList parameters, String value) {
            DefaultAlarm property = null;
            property = FALSE.getValue().equals(value) ? FALSE : new DefaultAlarm(parameters, value);
            return property;
        }
    }

    private static final class ImmutableDefaultAlarm
    extends DefaultAlarm {
        private static final long serialVersionUID = -2054338254L;

        private ImmutableDefaultAlarm(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

