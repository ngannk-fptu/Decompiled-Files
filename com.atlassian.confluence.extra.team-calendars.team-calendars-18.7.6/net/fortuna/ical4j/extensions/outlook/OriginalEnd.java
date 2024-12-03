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

public class OriginalEnd
extends DateProperty {
    private static final long serialVersionUID = -4581216613476177094L;
    public static final String PROPERTY_NAME = "X-MS-OLK-ORIGINALEND";

    public OriginalEnd() {
        super(PROPERTY_NAME, new Factory());
    }

    public OriginalEnd(ParameterList aList, String value) throws ParseException {
        super(PROPERTY_NAME, aList, (PropertyFactory)new Factory());
        this.setValue(value);
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<OriginalEnd> {
        private static final long serialVersionUID = 596282786680252116L;

        public Factory() {
            super(OriginalEnd.PROPERTY_NAME);
        }

        @Override
        public OriginalEnd createProperty() {
            return new OriginalEnd();
        }

        @Override
        public OriginalEnd createProperty(ParameterList parameters, String value) throws ParseException {
            OriginalEnd property = new OriginalEnd(parameters, value);
            return property;
        }
    }
}

