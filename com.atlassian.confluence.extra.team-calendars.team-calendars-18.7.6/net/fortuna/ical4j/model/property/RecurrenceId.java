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
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class RecurrenceId
extends DateProperty {
    private static final long serialVersionUID = 4456883817126011006L;

    public RecurrenceId() {
        super("RECURRENCE-ID", new Factory());
        this.setDate(new DateTime());
    }

    public RecurrenceId(TimeZone timezone) {
        super("RECURRENCE-ID", timezone, (PropertyFactory)new Factory());
    }

    public RecurrenceId(String value) throws ParseException {
        super("RECURRENCE-ID", new Factory());
        this.setValue(value);
    }

    public RecurrenceId(String value, TimeZone timezone) throws ParseException {
        super("RECURRENCE-ID", timezone, (PropertyFactory)new Factory());
        this.setValue(value);
    }

    public RecurrenceId(ParameterList aList, String aValue) throws ParseException {
        super("RECURRENCE-ID", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public RecurrenceId(Date aDate) {
        super("RECURRENCE-ID", new Factory());
        this.setDate(aDate);
    }

    public RecurrenceId(ParameterList aList, Date aDate) {
        super("RECURRENCE-ID", aList, (PropertyFactory)new Factory());
        this.setDate(aDate);
    }

    @Override
    public final void validate() throws ValidationException {
        super.validate();
        ParameterValidator.assertOneOrLess("RANGE", this.getParameters());
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("RECURRENCE-ID");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new RecurrenceId(parameters, value);
        }

        public Property createProperty() {
            return new RecurrenceId();
        }
    }
}

