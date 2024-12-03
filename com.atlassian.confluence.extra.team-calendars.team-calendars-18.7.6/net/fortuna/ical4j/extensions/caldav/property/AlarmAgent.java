/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class AlarmAgent
extends Property {
    private static final long serialVersionUID = 2182103734645261668L;
    public static final String PROPERTY_NAME = "ALARM-AGENT";
    private String value;
    public static final AlarmAgent SERVER = new ImmutableAlarmAgent("SERVER");
    public static final AlarmAgent CLIENT = new ImmutableAlarmAgent("CLIENT");
    public static final AlarmAgent BOTH = new ImmutableAlarmAgent("BOTH");
    public static final AlarmAgent NONE = new ImmutableAlarmAgent("NONE");

    public AlarmAgent() {
        super(PROPERTY_NAME, new Factory());
    }

    public AlarmAgent(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList, new Factory());
        this.setValue(value);
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("AGENT-ID", this.getParameters());
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<AlarmAgent> {
        private static final long serialVersionUID = 2099427445505899578L;

        public Factory() {
            super(AlarmAgent.PROPERTY_NAME);
        }

        @Override
        public AlarmAgent createProperty() {
            return new AlarmAgent();
        }

        @Override
        public AlarmAgent createProperty(ParameterList parameters, String value) {
            AlarmAgent property = null;
            property = CLIENT.getValue().equals(value) ? CLIENT : new AlarmAgent(parameters, value);
            return property;
        }
    }

    private static final class ImmutableAlarmAgent
    extends AlarmAgent {
        private static final long serialVersionUID = -2054338254L;

        private ImmutableAlarmAgent(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

