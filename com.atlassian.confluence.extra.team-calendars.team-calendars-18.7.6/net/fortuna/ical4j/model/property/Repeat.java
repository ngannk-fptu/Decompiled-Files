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

public class Repeat
extends Property {
    private static final long serialVersionUID = -1765522613173314831L;
    private int count;

    public Repeat() {
        super("REPEAT", new Factory());
    }

    public Repeat(ParameterList aList, String aValue) {
        super("REPEAT", aList, new Factory());
        this.setValue(aValue);
    }

    public Repeat(int aCount) {
        super("REPEAT", new Factory());
        this.count = aCount;
    }

    public Repeat(ParameterList aList, int aCount) {
        super("REPEAT", aList, new Factory());
        this.count = aCount;
    }

    public final int getCount() {
        return this.count;
    }

    @Override
    public final void setValue(String aValue) {
        this.count = Integer.parseInt(aValue);
    }

    @Override
    public final String getValue() {
        return String.valueOf(this.getCount());
    }

    public final void setCount(int count) {
        this.count = count;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("REPEAT");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Repeat(parameters, value);
        }

        public Property createProperty() {
            return new Repeat();
        }
    }
}

