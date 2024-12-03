/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.CalendarValidatorFactory;
import net.fortuna.ical4j.validate.CalendarValidatorImpl;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class DefaultCalendarValidatorFactory
implements CalendarValidatorFactory {
    @Override
    public Validator<Calendar> newInstance() {
        return new CalendarValidatorImpl(new ValidationRule(ValidationRule.ValidationType.One, "PRODID", "VERSION"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CALSCALE", "METHOD"));
    }
}

