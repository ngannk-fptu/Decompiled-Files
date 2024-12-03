/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class WrCalDesc
extends Property {
    private static final long serialVersionUID = 4925485073475375164L;
    public static final String PROPERTY_NAME = "X-WR-CALDESC";
    private String value;

    public WrCalDesc() {
        super(PROPERTY_NAME, new Factory());
    }

    public WrCalDesc(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList, new Factory());
        this.setValue(value);
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("VALUE", this.getParameters());
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<WrCalDesc> {
        private static final long serialVersionUID = -7990613145503686965L;

        public Factory() {
            super(WrCalDesc.PROPERTY_NAME);
        }

        @Override
        public WrCalDesc createProperty() {
            return new WrCalDesc();
        }

        @Override
        public WrCalDesc createProperty(ParameterList parameters, String value) {
            WrCalDesc property = new WrCalDesc(parameters, value);
            return property;
        }
    }
}

