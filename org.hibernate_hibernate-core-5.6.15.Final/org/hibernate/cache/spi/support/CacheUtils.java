/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.boot.spi.SessionFactoryOptions;

public class CacheUtils {
    public static boolean isUnqualified(String regionName, SessionFactoryOptions options) {
        String prefix = options.getCacheRegionPrefix();
        if (prefix == null) {
            return true;
        }
        return !regionName.startsWith(prefix);
    }
}

