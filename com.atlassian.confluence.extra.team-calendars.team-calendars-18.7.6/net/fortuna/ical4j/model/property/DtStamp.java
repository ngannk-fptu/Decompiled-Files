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

public class DtStamp
extends UtcProperty
implements Comparable<DtStamp> {
    private static final long serialVersionUID = 7581197869433744070L;

    public DtStamp() {
        super("DTSTAMP", new Factory());
    }

    public DtStamp(String aValue) throws ParseException {
        this(new ParameterList(), aValue);
    }

    public DtStamp(ParameterList aList, String aValue) throws ParseException {
        super("DTSTAMP", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public DtStamp(DateTime aDate) {
        super("DTSTAMP", new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public DtStamp(ParameterList aList, DateTime aDate) {
        super("DTSTAMP", aList, (PropertyFactory)new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    @Override
    public int compareTo(DtStamp o) {
        return this.getDate().compareTo(o.getDate());
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DTSTAMP");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new DtStamp(parameters, value);
        }

        public Property createProperty() {
            return new DtStamp();
        }
    }
}

