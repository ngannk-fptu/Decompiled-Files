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

public class TzId
extends Property
implements Escapable {
    private static final long serialVersionUID = -522764921502407137L;
    public static final String PREFIX = "/";
    private String value;

    public TzId() {
        super("TZID", new Factory());
    }

    public TzId(String aValue) {
        super("TZID", new Factory());
        this.setValue(aValue);
    }

    public TzId(ParameterList aList, String aValue) {
        super("TZID", aList, new Factory());
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
            super("TZID");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new TzId(parameters, value);
        }

        public Property createProperty() {
            return new TzId();
        }
    }
}

