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

public class RecordId
extends Property {
    private static final long serialVersionUID = -3377034395408250616L;
    public static final String PROPERTY_NAME = "X-RECORDID";
    private String value;

    public RecordId() {
        super(PROPERTY_NAME, new Factory());
    }

    public RecordId(ParameterList aList, String value) {
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
    implements PropertyFactory<RecordId> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(RecordId.PROPERTY_NAME);
        }

        @Override
        public RecordId createProperty() {
            return new RecordId();
        }

        @Override
        public RecordId createProperty(ParameterList parameters, String value) {
            RecordId property = new RecordId(parameters, value);
            return property;
        }
    }
}

