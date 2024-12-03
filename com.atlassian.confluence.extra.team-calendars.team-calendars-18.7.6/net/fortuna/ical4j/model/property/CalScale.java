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
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;

public class CalScale
extends Property {
    private static final long serialVersionUID = 7446184786984981423L;
    public static final CalScale GREGORIAN = new ImmutableCalScale("GREGORIAN");
    private String value;

    public CalScale() {
        super("CALSCALE", new Factory());
    }

    public CalScale(String aValue) {
        super("CALSCALE", new Factory());
        this.value = aValue;
    }

    public CalScale(ParameterList aList, String aValue) {
        super("CALSCALE", aList, new Factory());
        this.value = aValue;
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    @Override
    public final void validate() throws ValidationException {
        if (CompatibilityHints.isHintEnabled("ical4j.validation.relaxed") ? !GREGORIAN.getValue().equalsIgnoreCase(this.value) : !GREGORIAN.getValue().equals(this.value)) {
            throw new ValidationException("Invalid value [" + this.value + "]");
        }
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<CalScale> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("CALSCALE");
        }

        @Override
        public CalScale createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            CalScale calScale = GREGORIAN.getValue().equals(value) ? GREGORIAN : new CalScale(parameters, value);
            return calScale;
        }

        @Override
        public CalScale createProperty() {
            return new CalScale();
        }
    }

    private static final class ImmutableCalScale
    extends CalScale {
        private static final long serialVersionUID = 1750949550694413878L;

        private ImmutableCalScale(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

