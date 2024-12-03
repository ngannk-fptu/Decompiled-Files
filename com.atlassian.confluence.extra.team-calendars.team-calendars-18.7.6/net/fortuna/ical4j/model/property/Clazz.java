/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Clazz
extends Property {
    private static final long serialVersionUID = 4939943639175551481L;
    public static final Clazz PUBLIC = new ImmutableClazz("PUBLIC");
    public static final Clazz PRIVATE = new ImmutableClazz("PRIVATE");
    public static final Clazz CONFIDENTIAL = new ImmutableClazz("CONFIDENTIAL");
    private String value;

    public Clazz() {
        super("CLASS", new Factory());
    }

    public Clazz(String aValue) {
        super("CLASS", new Factory());
        this.value = aValue;
    }

    public Clazz(ParameterList aList, String aValue) {
        super("CLASS", aList, new Factory());
        this.value = aValue;
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<Clazz> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("CLASS");
        }

        @Override
        public Clazz createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            Clazz clazz = CONFIDENTIAL.getValue().equals(value) ? CONFIDENTIAL : (PRIVATE.getValue().equals(value) ? PRIVATE : (PUBLIC.getValue().equals(value) ? PUBLIC : new Clazz(parameters, value)));
            return clazz;
        }

        @Override
        public Clazz createProperty() {
            return new Clazz();
        }
    }

    private static final class ImmutableClazz
    extends Clazz {
        private static final long serialVersionUID = 5978394762293365042L;

        private ImmutableClazz(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

