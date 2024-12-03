/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import java.util.Locale;
import org.hibernate.CacheMode;

public class CacheModeConverter {
    public static CacheMode fromXml(String name) {
        for (CacheMode mode : CacheMode.values()) {
            if (!mode.name().equalsIgnoreCase(name)) continue;
            return mode;
        }
        return CacheMode.NORMAL;
    }

    public static String toXml(CacheMode cacheMode) {
        return cacheMode.name().toLowerCase(Locale.ENGLISH);
    }
}

