/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Conference
extends Property {
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_NAME = "CONFERENCE";
    private String value;

    public Conference() {
        super(PROPERTY_NAME, new Factory());
    }

    public Conference(ParameterList params, String value) {
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
    implements PropertyFactory<Conference> {
        public Factory() {
            super(Conference.PROPERTY_NAME);
        }

        @Override
        public Conference createProperty() {
            return new Conference();
        }

        @Override
        public Conference createProperty(ParameterList parameters, String value) {
            Conference property = new Conference(parameters, value);
            return property;
        }
    }
}

