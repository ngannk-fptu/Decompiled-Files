/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.time.temporal.TemporalAmount;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import net.fortuna.ical4j.validate.ValidationException;

public class RefreshInterval
extends Property {
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_NAME = "REFRESH-INTERVAL";
    private TemporalAmountAdapter duration;

    public RefreshInterval() {
        super(PROPERTY_NAME, new Factory());
    }

    public RefreshInterval(ParameterList params, String value) {
        super(PROPERTY_NAME, params, new Factory());
        this.setValue(value);
    }

    public RefreshInterval(ParameterList params, TemporalAmount duration) {
        super(PROPERTY_NAME, params, new Factory());
        this.duration = new TemporalAmountAdapter(duration);
    }

    @Override
    public void setValue(String aValue) {
        this.duration = TemporalAmountAdapter.parse(aValue);
    }

    @Override
    public void validate() throws ValidationException {
    }

    @Override
    public String getValue() {
        return this.duration.toString();
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<RefreshInterval> {
        public Factory() {
            super(RefreshInterval.PROPERTY_NAME);
        }

        @Override
        public RefreshInterval createProperty() {
            return new RefreshInterval();
        }

        @Override
        public RefreshInterval createProperty(ParameterList parameters, String value) {
            RefreshInterval property = new RefreshInterval(parameters, value);
            return property;
        }
    }
}

