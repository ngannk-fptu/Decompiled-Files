/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.TimeZone;

public interface TimeZoneRegistry {
    public void register(TimeZone var1);

    public void register(TimeZone var1, boolean var2);

    public void clear();

    public TimeZone getTimeZone(String var1);
}

