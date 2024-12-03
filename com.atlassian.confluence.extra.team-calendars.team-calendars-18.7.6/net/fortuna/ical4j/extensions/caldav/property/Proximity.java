/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Proximity
extends Property {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "PROXIMITY";
    private String value;
    public static final Proximity ARRIVE = new ImmutableProximity("ARRIVE");
    public static final Proximity DEPART = new ImmutableProximity("DEPART");

    public Proximity() {
        super(PROPERTY_NAME, new Factory());
    }

    public Proximity(ParameterList aList, String value) {
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
    implements PropertyFactory<Proximity> {
        private static final long serialVersionUID = 2099427445505899578L;

        public Factory() {
            super(Proximity.PROPERTY_NAME);
        }

        @Override
        public Proximity createProperty() {
            return new Proximity();
        }

        @Override
        public Proximity createProperty(ParameterList parameters, String value) {
            Proximity property = null;
            property = DEPART.getValue().equals(value) ? DEPART : new Proximity(parameters, value);
            return property;
        }
    }

    private static final class ImmutableProximity
    extends Proximity {
        private static final long serialVersionUID = -2054338254L;

        private ImmutableProximity(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

