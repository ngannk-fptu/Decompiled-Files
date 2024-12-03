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

public class DtEnd
extends DateProperty {
    private static final long serialVersionUID = 8107416684717228297L;

    public DtEnd() {
        super("DTEND", new Factory());
    }

    public DtEnd(TimeZone timezone) {
        super("DTEND", timezone, (PropertyFactory)new Factory());
    }

    public DtEnd(String value) throws ParseException {
        super("DTEND", new Factory());
        this.setValue(value);
    }

    public DtEnd(String value, TimeZone timezone) throws ParseException {
        super("DTEND", timezone, (PropertyFactory)new Factory());
        this.setValue(value);
    }

    public DtEnd(ParameterList aList, String aValue) throws ParseException {
        super("DTEND", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public DtEnd(Date aDate) {
        super("DTEND", new Factory());
        this.setDate(aDate);
    }

    public DtEnd(Date time, boolean utc) {
        super("DTEND", new Factory());
        this.setDate(time);
        this.setUtc(utc);
    }

    public DtEnd(ParameterList aList, Date aDate) {
        super("DTEND", aList, (PropertyFactory)new Factory());
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DTEND");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new DtEnd(parameters, value);
        }

        public Property createProperty() {
            return new DtEnd();
        }
    }
}

