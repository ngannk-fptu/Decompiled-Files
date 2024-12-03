/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class VEventValidator
extends ComponentValidator<VEvent> {
    private final Validator<VAlarm> itipValidator = new ComponentValidator<VAlarm>(new ValidationRule(ValidationRule.ValidationType.One, "ACTION", "TRIGGER"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "DESCRIPTION", "DURATION", "REPEAT", "SUMMARY"));
    private final boolean alarmsAllowed;

    public VEventValidator(ValidationRule ... rules) {
        this(true, rules);
    }

    public VEventValidator(boolean alarmsAllowed, ValidationRule ... rules) {
        super(rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public void validate(VEvent target) throws ValidationException {
        super.validate(target);
        if (this.alarmsAllowed) {
            target.getAlarms().forEach(this.itipValidator::validate);
        } else {
            ComponentValidator.assertNone("VALARM", target.getAlarms());
        }
    }
}

