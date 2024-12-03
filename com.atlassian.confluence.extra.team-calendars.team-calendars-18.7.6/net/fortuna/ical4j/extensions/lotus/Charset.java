/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.lotus;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Charset
extends Property {
    private static final long serialVersionUID = -3514682572599864426L;
    public static final String PROPERTY_NAME = "X-LOTUS-CHARSET";
    public static final Charset UTF8 = new Charset(new ParameterList(true), "UTF-8");
    private String value;

    public Charset() {
        super(PROPERTY_NAME, new Factory());
    }

    public Charset(ParameterList aList, String value) {
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
    implements PropertyFactory<Charset> {
        private static final long serialVersionUID = 596282786680252116L;

        public Factory() {
            super(Charset.PROPERTY_NAME);
        }

        @Override
        public Charset createProperty() {
            return new Charset();
        }

        @Override
        public Charset createProperty(ParameterList parameters, String value) {
            Charset property = null;
            property = UTF8.getValue().equals(value) ? UTF8 : new Charset(parameters, value);
            return property;
        }
    }
}

