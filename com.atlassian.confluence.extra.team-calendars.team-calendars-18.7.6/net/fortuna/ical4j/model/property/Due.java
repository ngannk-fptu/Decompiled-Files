/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.property.DateProperty;

public class Due
extends DateProperty {
    private static final long serialVersionUID = -2965312347832730406L;

    public Due() {
        super("DUE", new Factory());
        this.setDate(new DateTime(true));
    }

    public Due(TimeZone timezone) {
        super("DUE", timezone, (PropertyFactory)new Factory());
    }

    public Due(String value) throws ParseException {
        super("DUE", new Factory());
        this.setValue(value);
    }

    public Due(String value, TimeZone timezone) throws ParseException {
        super("DUE", timezone, (PropertyFactory)new Factory());
        this.setValue(value);
    }

    public Due(ParameterList aList, String aValue) throws ParseException {
        super("DUE", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public Due(Date aDate) {
        super("DUE", new Factory());
        this.setDate(aDate);
    }

    public Due(ParameterList aList, Date aDate) {
        super("DUE", aList, (PropertyFactory)new Factory());
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DUE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Due(parameters, value);
        }

        public Property createProperty() {
            return new Due();
        }
    }
}

