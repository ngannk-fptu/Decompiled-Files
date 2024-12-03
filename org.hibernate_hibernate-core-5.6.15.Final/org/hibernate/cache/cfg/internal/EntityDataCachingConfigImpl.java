/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.internal;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.hibernate.cache.cfg.internal.AbstractDomainDataCachingConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.metamodel.model.domain.NavigableRole;

public class EntityDataCachingConfigImpl
extends AbstractDomainDataCachingConfig
implements EntityDataCachingConfig {
    private final NavigableRole navigableRole;
    private final Supplier<Comparator> versionComparatorAccess;
    private final boolean isEntityMutable;
    private final Set<NavigableRole> cachedTypes = new HashSet<NavigableRole>();

    public EntityDataCachingConfigImpl(NavigableRole rootEntityName, Supplier<Comparator> versionComparatorAccess, boolean isEntityMutable, AccessType accessType) {
        super(accessType);
        this.navigableRole = rootEntityName;
        this.versionComparatorAccess = versionComparatorAccess;
        this.isEntityMutable = isEntityMutable;
    }

    @Override
    public Supplier<Comparator> getVersionComparatorAccess() {
        return this.versionComparatorAccess;
    }

    @Override
    public boolean isMutable() {
        return this.isEntityMutable;
    }

    @Override
    public boolean isVersioned() {
        return this.getVersionComparatorAccess() != null;
    }

    @Override
    public NavigableRole getNavigableRole() {
        return this.navigableRole;
    }

    @Override
    public Set<NavigableRole> getCachedTypes() {
        return this.cachedTypes;
    }

    public void addCachedType(NavigableRole typeRole) {
        this.cachedTypes.add(typeRole);
    }
}

