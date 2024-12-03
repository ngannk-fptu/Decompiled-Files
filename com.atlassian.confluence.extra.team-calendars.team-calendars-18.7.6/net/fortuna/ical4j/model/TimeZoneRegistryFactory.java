/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.util.Optional;
import net.fortuna.ical4j.model.DefaultTimeZoneRegistryFactory;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.util.Configurator;

public abstract class TimeZoneRegistryFactory {
    public static final String KEY_FACTORY_CLASS = "net.fortuna.ical4j.timezone.registry";
    private static TimeZoneRegistryFactory instance;

    public static TimeZoneRegistryFactory getInstance() {
        return instance;
    }

    public abstract TimeZoneRegistry createRegistry();

    static {
        Optional<DefaultTimeZoneRegistryFactory> property = Configurator.getObjectProperty(KEY_FACTORY_CLASS);
        instance = property.orElse(new DefaultTimeZoneRegistryFactory());
    }
}

