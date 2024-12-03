/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.RegionFactory;

public abstract class AbstractRegion
implements Region {
    private final String name;
    private final RegionFactory regionFactory;

    public AbstractRegion(String name, RegionFactory regionFactory) {
        this.name = name;
        this.regionFactory = regionFactory;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RegionFactory getRegionFactory() {
        return this.regionFactory;
    }
}

