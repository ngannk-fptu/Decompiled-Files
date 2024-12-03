/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.property;

import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.UtcProperty;
import net.fortuna.ical4j.validate.ValidationException;

public class CalStart
extends UtcProperty {
    private static final long serialVersionUID = -1823078836099613956L;
    public static final String PROPERTY_NAME = "X-CALSTART";

    public CalStart() {
        super(PROPERTY_NAME, new Factory());
    }

    public CalStart(ParameterList aList, String value) throws ParseException {
        super(PROPERTY_NAME, aList, (PropertyFactory)new Factory());
        this.setValue(value);
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<CalStart> {
        private static final long serialVersionUID = 596282786680252116L;

        public Factory() {
            super(CalStart.PROPERTY_NAME);
        }

        @Override
        public CalStart createProperty() {
            return new CalStart();
        }

        @Override
        public CalStart createProperty(ParameterList parameters, String value) throws ParseException {
            CalStart property = new CalStart(parameters, value);
            return property;
        }
    }
}

