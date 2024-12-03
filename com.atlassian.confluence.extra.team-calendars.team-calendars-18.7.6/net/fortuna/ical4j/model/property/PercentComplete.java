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
import net.fortuna.ical4j.validate.ValidationException;

public class PercentComplete
extends Property {
    private static final long serialVersionUID = 7788138484983240112L;
    private int percentage;

    public PercentComplete() {
        super("PERCENT-COMPLETE", new Factory());
    }

    public PercentComplete(ParameterList aList, String aValue) {
        super("PERCENT-COMPLETE", aList, new Factory());
        this.setValue(aValue);
    }

    public PercentComplete(int aPercentage) {
        super("PERCENT-COMPLETE", new Factory());
        this.percentage = aPercentage;
    }

    public PercentComplete(ParameterList aList, int aPercentage) {
        super("PERCENT-COMPLETE", aList, new Factory());
        this.percentage = aPercentage;
    }

    public final int getPercentage() {
        return this.percentage;
    }

    @Override
    public final void setValue(String aValue) {
        this.percentage = Integer.parseInt(aValue);
    }

    @Override
    public final String getValue() {
        return String.valueOf(this.getPercentage());
    }

    public final void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public final void validate() throws ValidationException {
        if (this.percentage < 0 || this.percentage > 100) {
            throw new ValidationException(this.getName() + " with invalid value: " + this.percentage);
        }
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("PERCENT-COMPLETE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new PercentComplete(parameters, value);
        }

        public Property createProperty() {
            return new PercentComplete();
        }
    }
}

