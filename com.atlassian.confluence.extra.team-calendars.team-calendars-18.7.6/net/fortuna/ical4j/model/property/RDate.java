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
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class RDate
extends DateListProperty {
    private static final long serialVersionUID = -3320381650013860193L;
    private PeriodList periods;

    public RDate() {
        super("RDATE", new Factory());
        this.periods = new PeriodList(false, true);
    }

    public RDate(ParameterList aList, String aValue) throws ParseException {
        super("RDATE", aList, (PropertyFactory)new Factory());
        this.periods = new PeriodList(false, true);
        this.setValue(aValue);
    }

    public RDate(DateList dates) {
        super("RDATE", dates, (PropertyFactory)new Factory());
        this.periods = new PeriodList(false, true);
    }

    public RDate(ParameterList aList, DateList dates) {
        super("RDATE", aList, dates, new Factory());
        this.periods = new PeriodList(false, true);
    }

    public RDate(PeriodList periods) {
        super("RDATE", new DateList(true), (PropertyFactory)new Factory());
        this.periods = periods;
    }

    public RDate(ParameterList aList, PeriodList periods) {
        super("RDATE", aList, new DateList(true), new Factory());
        this.periods = periods;
    }

    @Override
    public final void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("VALUE", this.getParameters());
        Object valueParam = this.getParameter("VALUE");
        if (!(valueParam == null || Value.DATE_TIME.equals(valueParam) || Value.DATE.equals(valueParam) || Value.PERIOD.equals(valueParam))) {
            throw new ValidationException("Parameter [VALUE] is invalid");
        }
        ParameterValidator.assertOneOrLess("TZID", this.getParameters());
    }

    public final PeriodList getPeriods() {
        return this.periods;
    }

    @Override
    public final void setValue(String aValue) throws ParseException {
        if (Value.PERIOD.equals(this.getParameter("VALUE"))) {
            this.periods = new PeriodList(aValue);
        } else {
            super.setValue(aValue);
        }
    }

    @Override
    public final String getValue() {
        if (!(this.periods == null || this.periods.isEmpty() && this.periods.isUnmodifiable())) {
            return Strings.valueOf(this.getPeriods());
        }
        return super.getValue();
    }

    @Override
    public final void setTimeZone(TimeZone timezone) {
        if (!(this.periods == null || this.periods.isEmpty() && this.periods.isUnmodifiable())) {
            this.periods.setTimeZone(timezone);
        } else {
            super.setTimeZone(timezone);
        }
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("RDATE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new RDate(parameters, value);
        }

        public Property createProperty() {
            return new RDate();
        }
    }
}

