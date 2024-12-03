/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

public class CacheRegionDefinition {
    private final CacheRegionType regionType;
    private final String role;
    private final String usage;
    private final String region;
    private final boolean cacheLazy;

    public CacheRegionDefinition(CacheRegionType cacheType, String role, String usage, String region, boolean cacheLazy) {
        this.regionType = cacheType;
        this.role = role;
        this.usage = usage;
        this.region = region;
        this.cacheLazy = cacheLazy;
    }

    public CacheRegionType getRegionType() {
        return this.regionType;
    }

    public String getRole() {
        return this.role;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getRegion() {
        return this.region;
    }

    public boolean isCacheLazy() {
        return this.cacheLazy;
    }

    public static enum CacheRegionType {
        ENTITY,
        COLLECTION,
        QUERY;

    }
}

