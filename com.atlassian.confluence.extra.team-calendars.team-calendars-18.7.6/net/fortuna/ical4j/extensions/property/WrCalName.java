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

public class WrCalName
extends Property {
    private static final long serialVersionUID = 3529181417508181637L;
    public static final String PROPERTY_NAME = "X-WR-CALNAME";
    private String value;

    public WrCalName() {
        super(PROPERTY_NAME, new Factory());
    }

    public WrCalName(ParameterList aList, String value) {
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
    implements PropertyFactory<WrCalName> {
        private static final long serialVersionUID = -202687610325706085L;

        public Factory() {
            super(WrCalName.PROPERTY_NAME);
        }

        @Override
        public WrCalName createProperty() {
            return new WrCalName();
        }

        @Override
        public WrCalName createProperty(ParameterList parameters, String value) {
            WrCalName property = new WrCalName(parameters, value);
            return property;
        }
    }
}

