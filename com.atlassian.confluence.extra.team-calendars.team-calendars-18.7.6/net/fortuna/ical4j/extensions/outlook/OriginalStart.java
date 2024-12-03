/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.outlook;

import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.validate.ValidationException;

public class OriginalStart
extends DateProperty {
    private static final long serialVersionUID = -2369374600955575062L;
    public static final String PROPERTY_NAME = "X-MS-OLK-ORIGINALSTART";

    public OriginalStart() {
        super(PROPERTY_NAME, new Factory());
    }

    public OriginalStart(ParameterList aList, String value) throws ParseException {
        super(PROPERTY_NAME, aList, (PropertyFactory)new Factory());
        this.setValue(value);
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<OriginalStart> {
        private static final long serialVersionUID = 596282786680252116L;

        public Factory() {
            super(OriginalStart.PROPERTY_NAME);
        }

        @Override
        public OriginalStart createProperty() {
            return new OriginalStart();
        }

        @Override
        public OriginalStart createProperty(ParameterList parameters, String value) throws ParseException {
            OriginalStart property = new OriginalStart(parameters, value);
            return property;
        }
    }
}

