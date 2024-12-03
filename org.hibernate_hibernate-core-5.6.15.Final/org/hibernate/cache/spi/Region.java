/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.RegionFactory;

public interface Region {
    public String getName();

    public RegionFactory getRegionFactory();

    public void clear();

    public void destroy() throws CacheException;
}

