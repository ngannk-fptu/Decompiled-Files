/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class BusyType
extends Property {
    private static final long serialVersionUID = -5140360270562621159L;
    public static final BusyType BUSY = new ImmutableBusyType("BUSY");
    public static final BusyType BUSY_UNAVAILABLE = new ImmutableBusyType("BUSY-UNAVAILABLE");
    public static final BusyType BUSY_TENTATIVE = new ImmutableBusyType("BUSY-TENTATIVE");
    private String value;

    public BusyType() {
        super("BUSYTYPE", new Factory());
    }

    public BusyType(String aValue) {
        super("BUSYTYPE", new Factory());
        this.value = aValue;
    }

    public BusyType(ParameterList aList, String aValue) {
        super("BUSYTYPE", aList, new Factory());
        this.value = aValue;
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<BusyType> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("BUSYTYPE");
        }

        @Override
        public BusyType createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            BusyType busyType = BUSY.getValue().equals(value) ? BUSY : (BUSY_TENTATIVE.getValue().equals(value) ? BUSY_TENTATIVE : (BUSY_UNAVAILABLE.getValue().equals(value) ? BUSY_UNAVAILABLE : new BusyType(parameters, value)));
            return busyType;
        }

        @Override
        public BusyType createProperty() {
            return new BusyType();
        }
    }

    private static final class ImmutableBusyType
    extends BusyType {
        private static final long serialVersionUID = -2454749569982470433L;

        private ImmutableBusyType(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

