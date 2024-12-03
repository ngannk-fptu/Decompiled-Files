/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.Validator;

public interface CalendarValidatorFactory {
    public Validator<Calendar> newInstance();
}

