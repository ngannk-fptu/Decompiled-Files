/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import java.util.Arrays;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.Available;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

public class VAvailability
extends CalendarComponent {
    private static final long serialVersionUID = -3001603309266267258L;
    private ComponentList<Available> available;

    public VAvailability() {
        this(true);
    }

    public VAvailability(boolean initialise) {
        super("VAVAILABILITY");
        this.available = new ComponentList();
        if (initialise) {
            this.getProperties().add(new DtStamp());
        }
    }

    public VAvailability(PropertyList properties) {
        super("VAVAILABILITY", properties);
        this.available = new ComponentList();
    }

    public VAvailability(PropertyList properties, ComponentList<Available> available) {
        super("VAVAILABILITY", properties);
        this.available = available;
    }

    public final ComponentList<Available> getAvailable() {
        return this.available;
    }

    @Override
    public final String toString() {
        return "BEGIN:" + this.getName() + "\r\n" + this.getProperties() + this.getAvailable() + "END" + ':' + this.getName() + "\r\n";
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        Arrays.asList("DTSTART", "DTSTAMP", "UID").forEach(parameter -> PropertyValidator.assertOne(parameter, this.getProperties()));
        DtStart start = (DtStart)this.getProperty("DTSTART");
        if (Value.DATE.equals(start.getParameter("VALUE"))) {
            throw new ValidationException("Property [DTSTART] must be a " + Value.DATE_TIME);
        }
        if (this.getProperty("DTEND") != null) {
            PropertyValidator.assertOne("DTEND", this.getProperties());
            DtEnd end = (DtEnd)this.getProperty("DTEND");
            if (Value.DATE.equals(end.getParameter("VALUE"))) {
                throw new ValidationException("Property [DTEND] must be a " + Value.DATE_TIME);
            }
            if (this.getProperty("DURATION") != null) {
                throw new ValidationException("Only one of Property [DTEND] or [DURATION must appear a VAVAILABILITY");
            }
        }
        Arrays.asList("BUSYTYPE", "CREATED", "LAST-MODIFIED", "ORGANIZER", "SEQUENCE", "SUMMARY", "URL").forEach(property -> PropertyValidator.assertOneOrLess(property, this.getProperties()));
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        return null;
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VAvailability> {
        public Factory() {
            super("VAVAILABILITY");
        }

        @Override
        public VAvailability createComponent() {
            return new VAvailability(false);
        }

        @Override
        public VAvailability createComponent(PropertyList properties) {
            return new VAvailability(properties);
        }

        @Override
        public VAvailability createComponent(PropertyList properties, ComponentList subComponents) {
            return new VAvailability(properties, subComponents);
        }
    }
}

