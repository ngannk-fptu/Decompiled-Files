/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.outlook;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class BusyStatus
extends Property {
    private static final long serialVersionUID = -3514682572599864426L;
    public static final String PROPERTY_NAME = "X-MICROSOFT-CDO-BUSYSTATUS";
    public static final PropertyFactory FACTORY = new Factory();
    public static final BusyStatus BUSY = new BusyStatus(new ParameterList(true), "BUSY");
    private String value;

    public BusyStatus() {
        super(PROPERTY_NAME, new Factory());
    }

    public BusyStatus(ParameterList aList, String value) {
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
    implements PropertyFactory<BusyStatus> {
        private static final long serialVersionUID = 596282786680252116L;

        public Factory() {
            super(BusyStatus.PROPERTY_NAME);
        }

        @Override
        public BusyStatus createProperty() {
            return new BusyStatus();
        }

        @Override
        public BusyStatus createProperty(ParameterList parameters, String value) {
            BusyStatus property = null;
            property = BUSY.getValue().equals(value) ? BUSY : new BusyStatus(parameters, value);
            return property;
        }
    }
}

