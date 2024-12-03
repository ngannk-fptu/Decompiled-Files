/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.lotus;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class UpdateSubject
extends Property {
    private static final long serialVersionUID = -615091328274504900L;
    public static final String PROPERTY_NAME = "X-LOTUS-UPDATE-SUBJECT";
    private String value;

    public UpdateSubject() {
        super(PROPERTY_NAME, new Factory());
    }

    public UpdateSubject(ParameterList aList, String value) {
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
    implements PropertyFactory<UpdateSubject> {
        private static final long serialVersionUID = 2326651749315407915L;

        public Factory() {
            super(UpdateSubject.PROPERTY_NAME);
        }

        @Override
        public UpdateSubject createProperty() {
            return new UpdateSubject();
        }

        @Override
        public UpdateSubject createProperty(ParameterList parameters, String value) {
            UpdateSubject property = new UpdateSubject(parameters, value);
            return property;
        }
    }
}

