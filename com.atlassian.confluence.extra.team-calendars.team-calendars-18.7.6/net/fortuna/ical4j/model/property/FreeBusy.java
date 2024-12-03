/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class FreeBusy
extends Property {
    private static final long serialVersionUID = -6415954847619338567L;
    private PeriodList periods;

    public FreeBusy() {
        super("FREEBUSY", new Factory());
        this.periods = new PeriodList();
    }

    public FreeBusy(String aValue) throws ParseException {
        super("FREEBUSY", new Factory());
        this.setValue(aValue);
    }

    public FreeBusy(ParameterList aList, String aValue) throws ParseException {
        super("FREEBUSY", aList, new Factory());
        this.setValue(aValue);
    }

    public FreeBusy(PeriodList pList) {
        super("FREEBUSY", new Factory());
        if (!pList.isUtc()) {
            throw new IllegalArgumentException("Periods must be in UTC format");
        }
        this.periods = pList;
    }

    public FreeBusy(ParameterList aList, PeriodList pList) {
        super("FREEBUSY", aList, new Factory());
        if (!pList.isUtc()) {
            throw new IllegalArgumentException("Periods must be in UTC format");
        }
        this.periods = pList;
    }

    @Override
    public final void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("FBTYPE", this.getParameters());
        if (!this.periods.isUtc()) {
            throw new ValidationException("Periods must be in UTC format");
        }
    }

    public final PeriodList getPeriods() {
        return this.periods;
    }

    @Override
    public final void setValue(String aValue) throws ParseException {
        this.periods = new PeriodList(aValue);
    }

    @Override
    public final String getValue() {
        return this.getPeriods().toString();
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("FREEBUSY");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new FreeBusy(parameters, value);
        }

        public Property createProperty() {
            return new FreeBusy();
        }
    }
}

