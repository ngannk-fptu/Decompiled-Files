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

public class Transp
extends Property {
    private static final long serialVersionUID = 3801479657311785518L;
    public static final Transp OPAQUE = new ImmutableTransp("OPAQUE");
    public static final Transp TRANSPARENT = new ImmutableTransp("TRANSPARENT");
    private String value;

    public Transp() {
        super("TRANSP", new Factory());
    }

    public Transp(String aValue) {
        super("TRANSP", new Factory());
        this.value = aValue;
    }

    public Transp(ParameterList aList, String aValue) {
        super("TRANSP", aList, new Factory());
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
    implements PropertyFactory<Transp> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("TRANSP");
        }

        @Override
        public Transp createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            Transp transp = OPAQUE.getValue().equals(value) ? OPAQUE : (TRANSPARENT.getValue().equals(value) ? TRANSPARENT : new Transp(parameters, value));
            return transp;
        }

        @Override
        public Transp createProperty() {
            return new Transp();
        }
    }

    private static final class ImmutableTransp
    extends Transp {
        private static final long serialVersionUID = -6595830107310111996L;

        private ImmutableTransp(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

