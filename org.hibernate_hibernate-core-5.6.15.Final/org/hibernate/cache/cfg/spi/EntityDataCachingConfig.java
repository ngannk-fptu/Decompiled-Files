/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.spi;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Supplier;
import org.hibernate.cache.cfg.spi.DomainDataCachingConfig;
import org.hibernate.metamodel.model.domain.NavigableRole;

public interface EntityDataCachingConfig
extends DomainDataCachingConfig {
    @Override
    public boolean isVersioned();

    public Supplier<Comparator> getVersionComparatorAccess();

    public Set<NavigableRole> getCachedTypes();
}

