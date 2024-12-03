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

public class Importance
extends Property {
    private static final long serialVersionUID = 3044453382694356302L;
    public static final String PROPERTY_NAME = "X-MICROSOFT-CDO-IMPORTANCE";
    private String value;

    public Importance() {
        super(PROPERTY_NAME, new Factory());
    }

    public Importance(ParameterList aList, String value) {
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
    implements PropertyFactory<Importance> {
        public Factory() {
            super(Importance.PROPERTY_NAME);
        }

        @Override
        public Importance createProperty() {
            return new Importance();
        }

        @Override
        public Importance createProperty(ParameterList parameters, String value) {
            Importance property = new Importance(parameters, value);
            return property;
        }
    }
}

