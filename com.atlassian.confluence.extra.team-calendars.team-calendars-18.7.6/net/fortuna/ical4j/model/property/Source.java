/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Source
extends Property {
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_NAME = "SOURCE";
    private String value;

    public Source() {
        super(PROPERTY_NAME, new Factory());
    }

    public Source(ParameterList params, String value) {
        super(PROPERTY_NAME, params, new Factory());
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
    implements PropertyFactory<Source> {
        public Factory() {
            super(Source.PROPERTY_NAME);
        }

        @Override
        public Source createProperty() {
            return new Source();
        }

        @Override
        public Source createProperty(ParameterList parameters, String value) {
            Source property = new Source(parameters, value);
            return property;
        }
    }
}

