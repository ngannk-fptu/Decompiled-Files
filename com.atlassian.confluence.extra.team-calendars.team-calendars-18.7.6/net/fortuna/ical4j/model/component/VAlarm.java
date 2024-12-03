/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class VAlarm
extends CalendarComponent {
    private static final long serialVersionUID = -8193965477414653802L;
    private final Map<Action, Validator> actionValidators = new HashMap<Action, Validator>();

    public VAlarm() {
        super("VALARM");
        this.actionValidators.put(Action.AUDIO, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.OneOrLess, "ATTACH")));
        this.actionValidators.put(Action.DISPLAY, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION")));
        this.actionValidators.put(Action.EMAIL, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION", "SUMMARY"), new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE")));
        this.actionValidators.put(Action.PROCEDURE, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "ATTACH"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "DESCRIPTION")));
    }

    public VAlarm(PropertyList properties) {
        super("VALARM", properties);
        this.actionValidators.put(Action.AUDIO, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.OneOrLess, "ATTACH")));
        this.actionValidators.put(Action.DISPLAY, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION")));
        this.actionValidators.put(Action.EMAIL, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION", "SUMMARY"), new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE")));
        this.actionValidators.put(Action.PROCEDURE, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "ATTACH"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "DESCRIPTION")));
    }

    public VAlarm(DateTime trigger) {
        this();
        this.getProperties().add(new Trigger(trigger));
    }

    public VAlarm(TemporalAmount trigger) {
        this();
        this.getProperties().add(new Trigger(trigger));
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        PropertyValidator.assertOne("ACTION", this.getProperties());
        PropertyValidator.assertOne("TRIGGER", this.getProperties());
        PropertyValidator.assertOneOrLess("DURATION", this.getProperties());
        PropertyValidator.assertOneOrLess("REPEAT", this.getProperties());
        try {
            PropertyValidator.assertNone("DURATION", this.getProperties());
            PropertyValidator.assertNone("REPEAT", this.getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.assertOne("DURATION", this.getProperties());
            PropertyValidator.assertOne("REPEAT", this.getProperties());
        }
        Validator actionValidator = this.actionValidators.get(this.getAction());
        if (actionValidator != null) {
            actionValidator.validate(this);
        }
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        throw new UnsupportedOperationException("VALARM validation included in VEVENT or VTODO method validator.");
    }

    public final Action getAction() {
        return (Action)this.getProperty("ACTION");
    }

    public final Trigger getTrigger() {
        return (Trigger)this.getProperty("TRIGGER");
    }

    public final Duration getDuration() {
        return (Duration)this.getProperty("DURATION");
    }

    public final Repeat getRepeat() {
        return (Repeat)this.getProperty("REPEAT");
    }

    public final Attach getAttachment() {
        return (Attach)this.getProperty("ATTACH");
    }

    public final Description getDescription() {
        return (Description)this.getProperty("DESCRIPTION");
    }

    public final Summary getSummary() {
        return (Summary)this.getProperty("SUMMARY");
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VAlarm> {
        public Factory() {
            super("VALARM");
        }

        @Override
        public VAlarm createComponent() {
            return new VAlarm();
        }

        @Override
        public VAlarm createComponent(PropertyList properties) {
            return new VAlarm(properties);
        }

        @Override
        public VAlarm createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", "VALARM"));
        }
    }
}

