/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class RRule
extends Property {
    private static final long serialVersionUID = -9188265089143001164L;
    private Recur recur;

    public RRule() {
        super("RRULE", new Factory());
        this.recur = new Recur(Recur.Frequency.DAILY, 1);
    }

    public RRule(String value) throws ParseException {
        super("RRULE", new Factory());
        this.setValue(value);
    }

    public RRule(ParameterList aList, String aValue) throws ParseException {
        super("RRULE", aList, new Factory());
        this.setValue(aValue);
    }

    public RRule(Recur aRecur) {
        super("RRULE", new Factory());
        this.recur = aRecur;
    }

    public RRule(ParameterList aList, Recur aRecur) {
        super("RRULE", aList, new Factory());
        this.recur = aRecur;
    }

    public final Recur getRecur() {
        return this.recur;
    }

    @Override
    public final void setValue(String aValue) throws ParseException {
        this.recur = new Recur(aValue);
    }

    @Override
    public final String getValue() {
        return this.getRecur().toString();
    }

    @Override
    public void validate() throws ValidationException {
        ParameterValidator.assertNone("TZID", this.getParameters());
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("RRULE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new RRule(parameters, value);
        }

        public Property createProperty() {
            return new RRule();
        }
    }
}

