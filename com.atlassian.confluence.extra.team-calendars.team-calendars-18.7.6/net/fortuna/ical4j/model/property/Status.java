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

public class Status
extends Property {
    private static final long serialVersionUID = 7401102230299289898L;
    public static final Status VEVENT_TENTATIVE = new ImmutableStatus("TENTATIVE");
    public static final Status VEVENT_CONFIRMED = new ImmutableStatus("CONFIRMED");
    public static final Status VEVENT_CANCELLED = new ImmutableStatus("CANCELLED");
    public static final Status VTODO_NEEDS_ACTION = new ImmutableStatus("NEEDS-ACTION");
    public static final Status VTODO_COMPLETED = new ImmutableStatus("COMPLETED");
    public static final Status VTODO_IN_PROCESS = new ImmutableStatus("IN-PROCESS");
    public static final Status VTODO_CANCELLED = new ImmutableStatus("CANCELLED");
    public static final Status VJOURNAL_DRAFT = new ImmutableStatus("DRAFT");
    public static final Status VJOURNAL_FINAL = new ImmutableStatus("FINAL");
    public static final Status VJOURNAL_CANCELLED = new ImmutableStatus("CANCELLED");
    private String value;

    public Status() {
        super("STATUS", new Factory());
    }

    public Status(String aValue) {
        super("STATUS", new Factory());
        this.value = aValue;
    }

    public Status(ParameterList aList, String aValue) {
        super("STATUS", aList, new Factory());
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
    implements PropertyFactory<Status> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("STATUS");
        }

        @Override
        public Status createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            Status status = VEVENT_CANCELLED.getValue().equals(value) ? VEVENT_CANCELLED : (VEVENT_CONFIRMED.getValue().equals(value) ? VEVENT_CONFIRMED : (VEVENT_TENTATIVE.getValue().equals(value) ? VEVENT_TENTATIVE : (VJOURNAL_CANCELLED.getValue().equals(value) ? VJOURNAL_CANCELLED : (VJOURNAL_DRAFT.getValue().equals(value) ? VJOURNAL_DRAFT : (VJOURNAL_FINAL.getValue().equals(value) ? VJOURNAL_FINAL : (VTODO_CANCELLED.getValue().equals(value) ? VTODO_CANCELLED : (VTODO_COMPLETED.getValue().equals(value) ? VTODO_COMPLETED : (VTODO_IN_PROCESS.getValue().equals(value) ? VTODO_IN_PROCESS : (VTODO_NEEDS_ACTION.getValue().equals(value) ? VTODO_NEEDS_ACTION : new Status(parameters, value))))))))));
            return status;
        }

        @Override
        public Status createProperty() {
            return new Status();
        }
    }

    private static final class ImmutableStatus
    extends Status {
        private static final long serialVersionUID = 7771868877237685612L;

        private ImmutableStatus(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

