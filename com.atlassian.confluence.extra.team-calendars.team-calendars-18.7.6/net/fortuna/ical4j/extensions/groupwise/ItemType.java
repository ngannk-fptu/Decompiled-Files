/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.groupwise;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class ItemType
extends Property {
    private static final long serialVersionUID = 359692381730081304L;
    public static final String PROPERTY_NAME = "X-GWITEM-TYPE";
    public static final ItemType APPOINTMENT = new ItemType(new ParameterList(true), "APPOINTMENT");
    private String value;

    public ItemType() {
        super(PROPERTY_NAME, new Factory());
    }

    public ItemType(ParameterList aList, String value) {
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
    implements PropertyFactory<ItemType> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(ItemType.PROPERTY_NAME);
        }

        @Override
        public ItemType createProperty() {
            return new ItemType();
        }

        @Override
        public ItemType createProperty(ParameterList parameters, String value) {
            ItemType property = null;
            property = APPOINTMENT.getValue().equals(value) ? APPOINTMENT : new ItemType(parameters, value);
            return property;
        }
    }
}

