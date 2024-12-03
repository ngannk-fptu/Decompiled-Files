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

public class Acknowledged
extends UtcProperty {
    private static final long serialVersionUID = 596619479148598528L;

    public Acknowledged() {
        super("ACKNOWLEDGED", new Factory());
    }

    public Acknowledged(String aValue) throws ParseException {
        this(new ParameterList(), aValue);
    }

    public Acknowledged(ParameterList aList, String aValue) throws ParseException {
        super("ACKNOWLEDGED", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public Acknowledged(DateTime aDate) {
        super("ACKNOWLEDGED", new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public Acknowledged(ParameterList aList, DateTime aDate) {
        super("ACKNOWLEDGED", aList, (PropertyFactory)new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<Property> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("ACKNOWLEDGED");
        }

        @Override
        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Acknowledged(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Acknowledged();
        }
    }
}

