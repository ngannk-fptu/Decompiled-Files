/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import net.fortuna.ical4j.validate.ValidationException;

public class Duration
extends Property {
    private static final long serialVersionUID = 9144969653829796798L;
    private TemporalAmountAdapter duration;

    public Duration() {
        super("DURATION", new Factory());
    }

    public Duration(ParameterList aList, String aValue) {
        super("DURATION", aList, new Factory());
        this.setValue(aValue);
    }

    @Deprecated
    public Duration(Dur duration) {
        this(TemporalAmountAdapter.from(duration).getDuration());
    }

    public Duration(TemporalAmount duration) {
        super("DURATION", new Factory());
        this.duration = new TemporalAmountAdapter(duration);
    }

    @Deprecated
    public Duration(ParameterList aList, Dur duration) {
        this(aList, TemporalAmountAdapter.from(duration).getDuration());
    }

    public Duration(ParameterList aList, TemporalAmount duration) {
        super("DURATION", aList, new Factory());
        this.setDuration(duration);
    }

    public Duration(Date start, Date end) {
        super("DURATION", new Factory());
        this.setDuration(TemporalAmountAdapter.fromDateRange(start, end).getDuration());
    }

    public final TemporalAmount getDuration() {
        return this.duration.getDuration();
    }

    @Override
    public final void setValue(String aValue) {
        this.duration = TemporalAmountAdapter.parse(aValue);
    }

    @Override
    public final String getValue() {
        return this.duration.toString();
    }

    public final void setDuration(TemporalAmount duration) {
        this.duration = new TemporalAmountAdapter(duration);
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DURATION");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Duration(parameters, value);
        }

        public Property createProperty() {
            return new Duration();
        }
    }
}

