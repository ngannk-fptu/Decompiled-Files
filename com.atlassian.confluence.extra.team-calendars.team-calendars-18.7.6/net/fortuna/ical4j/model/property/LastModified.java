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

public class LastModified
extends UtcProperty {
    private static final long serialVersionUID = 5288572652052836062L;

    public LastModified() {
        super("LAST-MODIFIED", new Factory());
    }

    public LastModified(String aValue) throws ParseException {
        this(new ParameterList(), aValue);
    }

    public LastModified(ParameterList aList, String aValue) throws ParseException {
        super("LAST-MODIFIED", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public LastModified(DateTime aDate) {
        super("LAST-MODIFIED", new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public LastModified(ParameterList aList, DateTime aDate) {
        super("LAST-MODIFIED", aList, (PropertyFactory)new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("LAST-MODIFIED");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new LastModified(parameters, value);
        }

        public Property createProperty() {
            return new LastModified();
        }
    }
}

