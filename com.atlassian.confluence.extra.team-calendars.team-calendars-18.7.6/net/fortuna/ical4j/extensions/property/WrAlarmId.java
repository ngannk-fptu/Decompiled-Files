/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class WrAlarmId
extends Property {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "X-WR-ALARMID";
    private String value;

    public WrAlarmId() {
        super(PROPERTY_NAME, new Factory());
    }

    public WrAlarmId(ParameterList aList, String value) {
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
    implements PropertyFactory<WrAlarmId> {
        private static final long serialVersionUID = 2099427445505899578L;

        public Factory() {
            super(WrAlarmId.PROPERTY_NAME);
        }

        @Override
        public WrAlarmId createProperty() {
            return new WrAlarmId();
        }

        @Override
        public WrAlarmId createProperty(ParameterList parameters, String value) {
            WrAlarmId property = new WrAlarmId(parameters, value);
            return property;
        }
    }
}

