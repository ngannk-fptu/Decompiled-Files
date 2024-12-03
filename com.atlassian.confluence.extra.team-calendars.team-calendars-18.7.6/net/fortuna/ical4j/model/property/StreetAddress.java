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

public class StreetAddress
extends Property
implements Escapable {
    private static final long serialVersionUID = 6352997029056626656L;
    private String value;

    public StreetAddress() {
        super("STREET-ADDRESS", new Factory());
    }

    public StreetAddress(String aValue) {
        super("STREET-ADDRESS", new Factory());
        this.setValue(aValue);
    }

    public StreetAddress(ParameterList aList, String aValue) {
        super("STREET-ADDRESS", aList, new Factory());
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
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("STREET-ADDRESS");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new StreetAddress(parameters, value);
        }

        public Property createProperty() {
            return new StreetAddress();
        }
    }
}

