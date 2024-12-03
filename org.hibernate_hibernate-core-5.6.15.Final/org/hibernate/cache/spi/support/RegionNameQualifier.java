/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.boot.spi.SessionFactoryOptions;

public class RegionNameQualifier {
    public static final RegionNameQualifier INSTANCE = new RegionNameQualifier();

    public String qualify(String regionName, SessionFactoryOptions options) {
        String prefix = options.getCacheRegionPrefix();
        if (prefix == null) {
            return regionName;
        }
        return this.qualify(prefix, regionName);
    }

    public String qualify(String prefix, String regionName) {
        if (regionName.startsWith(prefix + '.')) {
            return regionName;
        }
        return prefix + '.' + regionName;
    }

    public boolean isQualified(String regionName, SessionFactoryOptions options) {
        return this.isQualified(options.getCacheRegionPrefix(), regionName);
    }

    public boolean isQualified(String prefix, String regionName) {
        return prefix != null && regionName.startsWith(prefix);
    }

    private RegionNameQualifier() {
    }
}

