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

public class ExtendedAddress
extends Property
implements Escapable {
    private static final long serialVersionUID = 6506776332370144983L;
    private String value;

    public ExtendedAddress() {
        super("EXTENDED-ADDRESS", new Factory());
    }

    public ExtendedAddress(String aValue) {
        super("EXTENDED-ADDRESS", new Factory());
        this.setValue(aValue);
    }

    public ExtendedAddress(ParameterList aList, String aValue) {
        super("EXTENDED-ADDRESS", aList, new Factory());
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
            super("EXTENDED-ADDRESS");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new ExtendedAddress(parameters, value);
        }

        public Property createProperty() {
            return new ExtendedAddress();
        }
    }
}

