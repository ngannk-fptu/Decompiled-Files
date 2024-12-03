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

public class Completed
extends UtcProperty {
    private static final long serialVersionUID = 6824213281785639181L;

    public Completed() {
        super("COMPLETED", new Factory());
    }

    public Completed(String aValue) throws ParseException {
        super("COMPLETED", new Factory());
        this.setValue(aValue);
    }

    public Completed(ParameterList aList, String aValue) throws ParseException {
        super("COMPLETED", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public Completed(DateTime aDate) {
        super("COMPLETED", new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public Completed(ParameterList aList, DateTime aDate) {
        super("COMPLETED", aList, (PropertyFactory)new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("COMPLETED");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Completed(parameters, value);
        }

        public Property createProperty() {
            return new Completed();
        }
    }
}

