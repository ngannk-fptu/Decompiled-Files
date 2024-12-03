/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.validate.EmptyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

public abstract class CalendarComponent
extends Component {
    private static final long serialVersionUID = -5832972592377720592L;
    protected static final Validator<CalendarComponent> EMPTY_VALIDATOR = new EmptyValidator<CalendarComponent>();

    public CalendarComponent(String name) {
        super(name);
    }

    public CalendarComponent(String name, PropertyList properties) {
        super(name, properties);
    }

    public final void validate(Method method) throws ValidationException {
        Validator<CalendarComponent> validator = this.getValidator(method);
        if (validator == null) {
            throw new ValidationException("Unsupported method: " + method);
        }
        validator.validate(this);
    }

    protected abstract Validator<CalendarComponent> getValidator(Method var1);

    public final void validatePublish() throws ValidationException {
        this.validate(Method.PUBLISH);
    }

    public final void validateRequest() throws ValidationException {
        this.validate(Method.REQUEST);
    }

    public final void validateReply() throws ValidationException {
        this.validate(Method.REPLY);
    }

    public final void validateAdd() throws ValidationException {
        this.validate(Method.ADD);
    }

    public final void validateCancel() throws ValidationException {
        this.validate(Method.CANCEL);
    }

    public final void validateRefresh() throws ValidationException {
        this.validate(Method.REFRESH);
    }

    public final void validateCounter() throws ValidationException {
        this.validate(Method.COUNTER);
    }

    public final void validateDeclineCounter() throws ValidationException {
        this.validate(Method.DECLINE_COUNTER);
    }
}

