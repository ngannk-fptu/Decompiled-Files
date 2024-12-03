/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import java.util.Arrays;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

public class VVenue
extends CalendarComponent {
    private static final long serialVersionUID = 4502423035501438515L;

    public VVenue() {
        super("VVENUE");
    }

    public VVenue(PropertyList properties) {
        super("VVENUE", properties);
    }

    @Override
    public final String toString() {
        return "BEGIN:" + this.getName() + "\r\n" + this.getProperties() + "END" + ':' + this.getName() + "\r\n";
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        PropertyValidator.assertOne("UID", this.getProperties());
        Arrays.asList("NAME", "DESCRIPTION", "STREET-ADDRESS", "EXTENDED-ADDRESS", "LOCALITY", "REGION", "COUNTRY", "POSTAL-CODE", "TZID", "GEO", "LOCATION-TYPE", "CATEGORIES", "DTSTAMP", "CREATED", "LAST-MODIFIED").forEach(property -> PropertyValidator.assertOneOrLess(property, this.getProperties()));
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        return EMPTY_VALIDATOR;
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VVenue> {
        public Factory() {
            super("VVENUE");
        }

        @Override
        public VVenue createComponent() {
            return new VVenue();
        }

        @Override
        public VVenue createComponent(PropertyList properties) {
            return new VVenue(properties);
        }

        @Override
        public VVenue createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", "VVENUE"));
        }
    }
}

