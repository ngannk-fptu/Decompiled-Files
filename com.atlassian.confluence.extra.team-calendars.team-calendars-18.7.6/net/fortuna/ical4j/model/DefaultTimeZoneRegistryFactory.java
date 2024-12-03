/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;

public class DefaultTimeZoneRegistryFactory
extends TimeZoneRegistryFactory {
    @Override
    public TimeZoneRegistry createRegistry() {
        return new TimeZoneRegistryImpl();
    }
}

