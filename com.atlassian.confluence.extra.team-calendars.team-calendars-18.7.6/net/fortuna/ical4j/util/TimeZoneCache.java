/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.component.VTimeZone;

public interface TimeZoneCache {
    public VTimeZone getTimezone(String var1);

    public boolean putIfAbsent(String var1, VTimeZone var2);

    public boolean containsId(String var1);

    public void clear();
}

