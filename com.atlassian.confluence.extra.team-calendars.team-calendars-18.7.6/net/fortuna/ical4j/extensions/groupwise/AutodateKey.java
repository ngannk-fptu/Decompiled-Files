/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.groupwise;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class AutodateKey
extends Property {
    private static final long serialVersionUID = -4722251591472186896L;
    public static final String PROPERTY_NAME = "X-GWAUTODATE-KEY";
    private String value;

    public AutodateKey() {
        super(PROPERTY_NAME, new Factory());
    }

    public AutodateKey(ParameterList aList, String value) {
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
    implements PropertyFactory<AutodateKey> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(AutodateKey.PROPERTY_NAME);
        }

        @Override
        public AutodateKey createProperty() {
            return new AutodateKey();
        }

        @Override
        public AutodateKey createProperty(ParameterList parameters, String value) {
            AutodateKey property = new AutodateKey(parameters, value);
            return property;
        }
    }
}

