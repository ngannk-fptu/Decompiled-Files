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

public class WrRelCalId
extends Property {
    private static final long serialVersionUID = 1102593718257055628L;
    public static final String PROPERTY_NAME = "X-WR-RELCALID";
    private String value;

    public WrRelCalId() {
        super(PROPERTY_NAME, new Factory());
    }

    public WrRelCalId(ParameterList aList, String value) {
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
    implements PropertyFactory<WrRelCalId> {
        private static final long serialVersionUID = -6362745894043821710L;

        public Factory() {
            super(WrRelCalId.PROPERTY_NAME);
        }

        @Override
        public WrRelCalId createProperty() {
            return new WrRelCalId();
        }

        @Override
        public WrRelCalId createProperty(ParameterList parameters, String value) {
            WrRelCalId property = new WrRelCalId(parameters, value);
            return property;
        }
    }
}

