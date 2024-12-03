/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.util.Optional;
import java.util.function.Supplier;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserImpl;
import net.fortuna.ical4j.util.Configurator;

public abstract class CalendarParserFactory
implements Supplier<CalendarParser> {
    public static final String KEY_FACTORY_CLASS = "net.fortuna.ical4j.parser";
    private static Supplier<CalendarParser> instance;

    public static Supplier<CalendarParser> getInstance() {
        return instance;
    }

    static {
        Optional<Supplier<CalendarParser>> property = Configurator.getObjectProperty(KEY_FACTORY_CLASS);
        instance = property.orElse(() -> new CalendarParserImpl());
    }
}

