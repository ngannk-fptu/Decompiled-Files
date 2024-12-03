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

public class MessageId
extends Property {
    private static final long serialVersionUID = -7910360817210293089L;
    public static final String PROPERTY_NAME = "X-GWMESSAGEID";
    private String value;

    public MessageId() {
        super(PROPERTY_NAME, new Factory());
    }

    public MessageId(ParameterList aList, String value) {
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
    implements PropertyFactory<MessageId> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(MessageId.PROPERTY_NAME);
        }

        @Override
        public MessageId createProperty() {
            return new MessageId();
        }

        @Override
        public MessageId createProperty(ParameterList parameters, String value) {
            MessageId property = new MessageId(parameters, value);
            return property;
        }
    }
}

