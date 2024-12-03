/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.groupwise;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class ShowAs
extends Property {
    private static final long serialVersionUID = 1777126874405580074L;
    public static final String PROPERTY_NAME = "X-GWSHOW-AS";
    public static final ShowAs BUSY = new ShowAs(new ParameterList(true), "BUSY");
    private String value;

    public ShowAs() {
        super(PROPERTY_NAME, new Factory());
    }

    public ShowAs(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList, new Factory());
        this.setValue(value);
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public void validate() throws ValidationException {
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<ShowAs> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(ShowAs.PROPERTY_NAME);
        }

        @Override
        public ShowAs createProperty() {
            return new ShowAs();
        }

        @Override
        public ShowAs createProperty(ParameterList parameters, String value) {
            ShowAs property = null;
            property = BUSY.getValue().equals(value) ? BUSY : new ShowAs(parameters, value);
            return property;
        }
    }
}

