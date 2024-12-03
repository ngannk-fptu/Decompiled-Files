/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Postalcode
extends Property
implements Escapable {
    private static final long serialVersionUID = 1983456638722378724L;
    private String value;

    public Postalcode() {
        super("POSTAL-CODE", new Factory());
    }

    public Postalcode(String aValue) {
        super("POSTAL-CODE", new Factory());
        this.setValue(aValue);
    }

    public Postalcode(ParameterList aList, String aValue) {
        super("POSTAL-CODE", aList, new Factory());
        this.setValue(aValue);
    }

    @Override
    public final void setValue(String aValue) {
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
    implements PropertyFactory<Postalcode> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("POSTAL-CODE");
        }

        @Override
        public Postalcode createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Postalcode(parameters, value);
        }

        @Override
        public Postalcode createProperty() {
            return new Postalcode();
        }
    }
}

