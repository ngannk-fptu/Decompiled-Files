/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.spi;

import java.util.List;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;

public interface DomainDataRegionConfig {
    public String getRegionName();

    public List<EntityDataCachingConfig> getEntityCaching();

    public List<NaturalIdDataCachingConfig> getNaturalIdCaching();

    public List<CollectionDataCachingConfig> getCollectionCaching();
}

