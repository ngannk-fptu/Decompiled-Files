/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model;

import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.internal.util.StringHelper;

public class Caching {
    private TruthValue requested;
    private String region;
    private AccessType accessType;
    private boolean cacheLazyProperties;

    public Caching(TruthValue requested) {
        this.requested = requested;
    }

    public Caching(String region, AccessType accessType, boolean cacheLazyProperties) {
        this(region, accessType, cacheLazyProperties, TruthValue.UNKNOWN);
    }

    public Caching(String region, AccessType accessType, boolean cacheLazyProperties, TruthValue requested) {
        this.requested = requested;
        this.region = region;
        this.accessType = accessType;
        this.cacheLazyProperties = cacheLazyProperties;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public AccessType getAccessType() {
        return this.accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public boolean isCacheLazyProperties() {
        return this.cacheLazyProperties;
    }

    public void setCacheLazyProperties(boolean cacheLazyProperties) {
        this.cacheLazyProperties = cacheLazyProperties;
    }

    public TruthValue getRequested() {
        return this.requested;
    }

    public void setRequested(TruthValue requested) {
        this.requested = requested;
    }

    public void overlay(CacheRegionDefinition overrides) {
        if (overrides == null) {
            return;
        }
        this.requested = TruthValue.TRUE;
        this.accessType = AccessType.fromExternalName(overrides.getUsage());
        if (StringHelper.isEmpty(overrides.getRegion())) {
            this.region = overrides.getRegion();
        }
        this.cacheLazyProperties = overrides.isCacheLazy();
    }

    public void overlay(Caching overrides) {
        if (overrides == null) {
            return;
        }
        this.requested = overrides.requested;
        this.accessType = overrides.accessType;
        this.region = overrides.region;
        this.cacheLazyProperties = overrides.cacheLazyProperties;
    }

    public String toString() {
        return "Caching{region='" + this.region + '\'' + ", accessType=" + (Object)((Object)this.accessType) + ", cacheLazyProperties=" + this.cacheLazyProperties + ", requested=" + (Object)((Object)this.requested) + '}';
    }
}

