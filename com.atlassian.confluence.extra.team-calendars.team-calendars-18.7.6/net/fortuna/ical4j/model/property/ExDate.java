/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class ExDate
extends DateListProperty {
    private static final long serialVersionUID = 2635730172243974463L;

    public ExDate() {
        super("EXDATE", new Factory());
    }

    public ExDate(ParameterList aList, String aValue) throws ParseException {
        super("EXDATE", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public ExDate(DateList dList) {
        super("EXDATE", dList, (PropertyFactory)new Factory());
    }

    public ExDate(ParameterList aList, DateList dList) {
        super("EXDATE", aList, dList, new Factory());
    }

    @Override
    public final void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("VALUE", this.getParameters());
        Object valueParam = this.getParameter("VALUE");
        if (valueParam != null && !Value.DATE_TIME.equals(valueParam) && !Value.DATE.equals(valueParam)) {
            throw new ValidationException("Parameter [VALUE] is invalid");
        }
        ParameterValidator.assertOneOrLess("TZID", this.getParameters());
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("EXDATE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new ExDate(parameters, value);
        }

        public Property createProperty() {
            return new ExDate();
        }
    }
}

