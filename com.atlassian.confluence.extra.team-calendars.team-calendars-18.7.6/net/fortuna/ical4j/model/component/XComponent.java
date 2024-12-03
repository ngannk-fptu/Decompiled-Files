/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

public class XComponent
extends CalendarComponent {
    private static final long serialVersionUID = -3622674849097714927L;

    public XComponent(String name) {
        super(name);
    }

    public XComponent(String name, PropertyList properties) {
        super(name, properties);
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed") && !this.getName().startsWith("X-")) {
            throw new ValidationException("Experimental components must have the following prefix: X-");
        }
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        return EMPTY_VALIDATOR;
    }
}

