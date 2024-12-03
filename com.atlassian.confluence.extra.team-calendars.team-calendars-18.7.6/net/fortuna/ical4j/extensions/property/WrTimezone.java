/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class WrTimezone
extends Property {
    private static final long serialVersionUID = 7248705823074186148L;
    public static final String PROPERTY_NAME = "X-WR-TIMEZONE";
    private String value;

    public WrTimezone() {
        super(PROPERTY_NAME, new Factory());
    }

    public WrTimezone(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList, new Factory());
        this.setValue(value);
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("VALUE", this.getParameters());
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<WrTimezone> {
        private static final long serialVersionUID = 3538377735326578201L;

        public Factory() {
            super(WrTimezone.PROPERTY_NAME);
        }

        @Override
        public WrTimezone createProperty() {
            return new WrTimezone();
        }

        @Override
        public WrTimezone createProperty(ParameterList parameters, String value) {
            WrTimezone property = new WrTimezone(parameters, value);
            return property;
        }
    }
}

