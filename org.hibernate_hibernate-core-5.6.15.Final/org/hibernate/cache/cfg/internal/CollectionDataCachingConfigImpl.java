/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.internal;

import java.util.Comparator;
import org.hibernate.cache.cfg.internal.AbstractDomainDataCachingConfig;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.mapping.Collection;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.type.VersionType;

public class CollectionDataCachingConfigImpl
extends AbstractDomainDataCachingConfig
implements CollectionDataCachingConfig {
    private final Collection collectionDescriptor;
    private final NavigableRole navigableRole;

    public CollectionDataCachingConfigImpl(Collection collectionDescriptor, AccessType accessType) {
        super(accessType);
        this.collectionDescriptor = collectionDescriptor;
        this.navigableRole = new NavigableRole(collectionDescriptor.getRole());
    }

    @Override
    public boolean isMutable() {
        return this.collectionDescriptor.isMutable();
    }

    @Override
    public boolean isVersioned() {
        return this.collectionDescriptor.getOwner().isVersioned();
    }

    @Override
    public Comparator getOwnerVersionComparator() {
        if (!this.isVersioned()) {
            return null;
        }
        return ((VersionType)this.collectionDescriptor.getOwner().getVersion().getType()).getComparator();
    }

    @Override
    public NavigableRole getNavigableRole() {
        return this.navigableRole;
    }
}

