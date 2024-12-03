/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class VTimeZoneValidator
extends ComponentValidator<VTimeZone> {
    private final Validator itipValidator = new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTART", "TZOFFSETFROM", "TZOFFSETTO"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "TZNAME"));

    public VTimeZoneValidator(ValidationRule ... rules) {
        super(rules);
    }

    @Override
    public void validate(VTimeZone target) throws ValidationException {
        super.validate(target);
        target.getObservances().forEach(this.itipValidator::validate);
    }
}

