/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import java.util.Arrays;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class Available
extends Component {
    private static final long serialVersionUID = -2494710612002978763L;

    public Available() {
        super("AVAILABLE");
    }

    public Available(PropertyList properties) {
        super("AVAILABLE", properties);
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        Arrays.asList("DTSTART", "DTSTAMP", "UID").forEach(property -> PropertyValidator.assertOne(property, this.getProperties()));
        DtStart start = (DtStart)this.getProperty("DTSTART");
        if (Value.DATE.equals(start.getParameter("VALUE"))) {
            throw new ValidationException("Property [DTSTART] must be a " + Value.DATE_TIME);
        }
        Arrays.asList("CREATED", "LAST-MODIFIED", "RECURRENCE-ID", "RRULE", "SUMMARY").forEach(property -> PropertyValidator.assertOneOrLess(property, this.getProperties()));
        if (this.getProperty("DTEND") != null) {
            PropertyValidator.assertOne("DTEND", this.getProperties());
            DtEnd end = (DtEnd)this.getProperty("DTEND");
            if (Value.DATE.equals(end.getParameter("VALUE"))) {
                throw new ValidationException("Property [DTEND] must be a " + Value.DATE_TIME);
            }
        } else {
            PropertyValidator.assertOne("DURATION", this.getProperties());
        }
        if (recurse) {
            this.validateProperties();
        }
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<Available> {
        public Factory() {
            super("AVAILABLE");
        }

        @Override
        public Available createComponent() {
            return new Available();
        }

        @Override
        public Available createComponent(PropertyList properties) {
            return new Available(properties);
        }

        @Override
        public Available createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", "AVAILABLE"));
        }
    }
}

