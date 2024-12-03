/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.UtcProperty;

public class Created
extends UtcProperty {
    private static final long serialVersionUID = -8658935097721652961L;

    public Created() {
        super("CREATED", new Factory());
    }

    public Created(String aValue) throws ParseException {
        super("CREATED", new Factory());
        this.setValue(aValue);
    }

    public Created(ParameterList aList, String aValue) throws ParseException {
        super("CREATED", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public Created(DateTime aDate) {
        super("CREATED", new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public Created(ParameterList aList, DateTime aDate) {
        super("CREATED", aList, (PropertyFactory)new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("CREATED");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Created(parameters, value);
        }

        public Property createProperty() {
            return new Created();
        }
    }
}

