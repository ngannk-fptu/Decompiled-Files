/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.temporal.TemporalAmount;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.UtcProperty;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class Trigger
extends UtcProperty {
    private static final long serialVersionUID = 5049421499261722194L;
    private TemporalAmountAdapter duration;

    public Trigger() {
        super("TRIGGER", new Factory());
    }

    public Trigger(ParameterList aList, String aValue) {
        super("TRIGGER", aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    @Deprecated
    public Trigger(Dur duration) {
        this(TemporalAmountAdapter.from(duration).getDuration());
    }

    public Trigger(TemporalAmount duration) {
        super("TRIGGER", new Factory());
        this.setDuration(duration);
    }

    @Deprecated
    public Trigger(ParameterList aList, Dur duration) {
        this(aList, TemporalAmountAdapter.from(duration).getDuration());
    }

    public Trigger(ParameterList aList, TemporalAmount duration) {
        super("TRIGGER", aList, (PropertyFactory)new Factory());
        this.setDuration(duration);
    }

    public Trigger(DateTime dateTime) {
        super("TRIGGER", new Factory());
        this.setDateTime(dateTime);
    }

    public Trigger(ParameterList aList, DateTime dateTime) {
        super("TRIGGER", aList, (PropertyFactory)new Factory());
        this.setDateTime(dateTime);
    }

    @Override
    public final void validate() throws ValidationException {
        super.validate();
        Object relParam = this.getParameter("RELATED");
        Object valueParam = this.getParameter("VALUE");
        if (relParam != null || !Value.DATE_TIME.equals(valueParam)) {
            ParameterValidator.assertOneOrLess("RELATED", this.getParameters());
            ParameterValidator.assertNullOrEqual(Value.DURATION, this.getParameters());
            if (this.getDuration() == null) {
                throw new ValidationException("Duration value not specified");
            }
        } else {
            ParameterValidator.assertOne("VALUE", this.getParameters());
            ParameterValidator.assertNullOrEqual(Value.DATE_TIME, this.getParameters());
            if (this.getDateTime() == null) {
                throw new ValidationException("DATE-TIME value not specified");
            }
        }
    }

    public final TemporalAmount getDuration() {
        if (this.duration != null) {
            return this.duration.getDuration();
        }
        return null;
    }

    @Override
    public final void setValue(String aValue) {
        try {
            super.setValue(aValue);
            this.duration = null;
        }
        catch (ParseException pe) {
            this.duration = TemporalAmountAdapter.parse(aValue);
            super.setDateTime(null);
        }
    }

    @Override
    public final String getValue() {
        if (this.duration != null) {
            return this.duration.toString();
        }
        return super.getValue();
    }

    @Override
    public final void setDateTime(DateTime dateTime) {
        super.setDateTime(dateTime);
        this.duration = null;
        this.getParameters().replace(Value.DATE_TIME);
    }

    public final void setDuration(TemporalAmount duration) {
        this.duration = new TemporalAmountAdapter(duration);
        super.setDateTime(null);
        if (this.getParameter("VALUE") != null) {
            this.getParameters().replace(Value.DURATION);
        }
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("TRIGGER");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Trigger(parameters, value);
        }

        public Property createProperty() {
            return new Trigger();
        }
    }
}

