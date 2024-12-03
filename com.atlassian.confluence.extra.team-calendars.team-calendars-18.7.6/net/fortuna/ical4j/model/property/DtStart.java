/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.property.DateProperty;

public class DtStart
extends DateProperty {
    private static final long serialVersionUID = -5707097476081111815L;

    public DtStart() {
        super("DTSTART", new Factory());
    }

    public DtStart(TimeZone timezone) {
        super("DTSTART", timezone, (PropertyFactory)new Factory());
    }

    public DtStart(String aValue) throws ParseException {
        super("DTSTART", new Factory());
        this.setValue(aValue);
    }

    public DtStart(String value, TimeZone timezone) throws ParseException {
        super("DTSTART", timezone, (PropertyFactory)new Factory());
        this.setValue(value);
    }

    public DtStart(ParameterList aList, String aValue) throws ParseException {
        super("DTSTART", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public DtStart(Date aDate) {
        super("DTSTART", new Factory());
        this.setDate(aDate);
    }

    public DtStart(Date time, boolean utc) {
        super("DTSTART", new Factory());
        this.setDate(time);
        this.setUtc(utc);
    }

    public DtStart(ParameterList aList, Date aDate) {
        super("DTSTART", aList, (PropertyFactory)new Factory());
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DTSTART");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new DtStart(parameters, value);
        }

        public Property createProperty() {
            return new DtStart();
        }
    }
}

