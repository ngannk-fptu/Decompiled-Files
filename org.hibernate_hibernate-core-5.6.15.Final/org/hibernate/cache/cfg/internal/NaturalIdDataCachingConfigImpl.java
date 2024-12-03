/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.internal;

import java.util.Iterator;
import org.hibernate.cache.cfg.internal.AbstractDomainDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.metamodel.model.domain.NavigableRole;

public class NaturalIdDataCachingConfigImpl
extends AbstractDomainDataCachingConfig
implements NaturalIdDataCachingConfig {
    private final RootClass rootEntityDescriptor;
    private final NavigableRole navigableRole;
    private final boolean mutable;

    public NaturalIdDataCachingConfigImpl(RootClass rootEntityDescriptor, AccessType accessType) {
        super(accessType);
        this.rootEntityDescriptor = rootEntityDescriptor;
        this.navigableRole = new NavigableRole(rootEntityDescriptor.getEntityName());
        this.mutable = this.hasAnyMutableNaturalIdProps();
    }

    private boolean hasAnyMutableNaturalIdProps() {
        Iterator itr = this.rootEntityDescriptor.getDeclaredPropertyIterator();
        while (itr.hasNext()) {
            Property prop = (Property)itr.next();
            if (!prop.isNaturalIdentifier() || !prop.isUpdateable()) continue;
            return true;
        }
        return false;
    }

    @Override
    public NavigableRole getNavigableRole() {
        return this.navigableRole;
    }

    @Override
    public boolean isMutable() {
        return this.mutable;
    }

    @Override
    public boolean isVersioned() {
        return false;
    }
}

