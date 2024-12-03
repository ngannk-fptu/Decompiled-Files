/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.spi;

import java.util.Comparator;
import org.hibernate.cache.cfg.spi.DomainDataCachingConfig;

public interface CollectionDataCachingConfig
extends DomainDataCachingConfig {
    public Comparator getOwnerVersionComparator();
}

