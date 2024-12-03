/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.property;

import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.UtcProperty;

public class Acknowledged
extends UtcProperty {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "ACKNOWLEDGED";

    public Acknowledged() {
        super(PROPERTY_NAME, new Factory());
    }

    public Acknowledged(String aValue) throws ParseException {
        this(new ParameterList(), aValue);
    }

    public Acknowledged(ParameterList aList, String aValue) throws ParseException {
        super(PROPERTY_NAME, aList, (PropertyFactory)new Factory());
        this.setValue(aValue);
    }

    public Acknowledged(DateTime aDate) {
        super(PROPERTY_NAME, new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public Acknowledged(ParameterList aList, DateTime aDate) {
        super(PROPERTY_NAME, aList, (PropertyFactory)new Factory());
        aDate.setUtc(true);
        this.setDate(aDate);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<Acknowledged> {
        public Factory() {
            super(Acknowledged.PROPERTY_NAME);
        }

        @Override
        public Acknowledged createProperty() {
            return new Acknowledged();
        }

        @Override
        public Acknowledged createProperty(ParameterList parameters, String value) throws ParseException {
            return new Acknowledged(parameters, value);
        }
    }
}

