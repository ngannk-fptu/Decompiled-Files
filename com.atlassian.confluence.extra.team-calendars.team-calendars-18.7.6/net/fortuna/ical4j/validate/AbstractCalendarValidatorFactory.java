/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import java.util.ServiceLoader;
import net.fortuna.ical4j.validate.CalendarValidatorFactory;
import net.fortuna.ical4j.validate.DefaultCalendarValidatorFactory;

public abstract class AbstractCalendarValidatorFactory {
    private static CalendarValidatorFactory instance = ServiceLoader.load(CalendarValidatorFactory.class, DefaultCalendarValidatorFactory.class.getClassLoader()).iterator().next();

    public static CalendarValidatorFactory getInstance() {
        return instance;
    }
}

