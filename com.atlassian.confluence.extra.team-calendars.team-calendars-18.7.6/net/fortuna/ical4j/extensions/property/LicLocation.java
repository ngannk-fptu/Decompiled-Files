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

public class LicLocation
extends Property {
    private static final long serialVersionUID = -9063118211308272499L;
    public static final String PROPERTY_NAME = "X-LIC-LOCATION";
    private String value;

    public LicLocation() {
        super(PROPERTY_NAME, new Factory());
    }

    public LicLocation(ParameterList aList, String value) {
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
    implements PropertyFactory<LicLocation> {
        public Factory() {
            super(LicLocation.PROPERTY_NAME);
        }

        @Override
        public LicLocation createProperty() {
            return new LicLocation();
        }

        @Override
        public LicLocation createProperty(ParameterList parameters, String value) {
            LicLocation property = new LicLocation(parameters, value);
            return property;
        }
    }
}

