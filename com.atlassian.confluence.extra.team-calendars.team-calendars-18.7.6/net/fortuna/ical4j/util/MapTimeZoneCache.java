/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.TimeZoneCache;

public class MapTimeZoneCache
implements TimeZoneCache {
    private final Map<String, VTimeZone> mapCache = new ConcurrentHashMap<String, VTimeZone>();

    @Override
    public VTimeZone getTimezone(String id) {
        return this.mapCache.get(id);
    }

    @Override
    public boolean putIfAbsent(String id, VTimeZone timeZone) {
        VTimeZone v = this.mapCache.get(id);
        if (v == null) {
            v = this.mapCache.put(id, timeZone);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsId(String id) {
        return this.mapCache.containsKey(id);
    }

    @Override
    public void clear() {
        this.mapCache.clear();
    }
}

