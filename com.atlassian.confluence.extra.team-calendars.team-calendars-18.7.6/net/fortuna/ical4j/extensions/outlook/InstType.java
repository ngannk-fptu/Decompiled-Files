/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.outlook;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class InstType
extends Property {
    private static final long serialVersionUID = -3495027929173808410L;
    public static final String PROPERTY_NAME = "X-MICROSOFT-CDO-INSTTYPE";
    private String value;

    public InstType() {
        super(PROPERTY_NAME, new Factory());
    }

    public InstType(ParameterList aList, String value) {
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
    implements PropertyFactory<InstType> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(InstType.PROPERTY_NAME);
        }

        @Override
        public InstType createProperty() {
            return new InstType();
        }

        @Override
        public InstType createProperty(ParameterList parameters, String value) {
            InstType property = new InstType(parameters, value);
            return property;
        }
    }
}

