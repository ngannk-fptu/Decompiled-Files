/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.spi;

import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.metamodel.model.domain.NavigableRole;

public interface DomainDataCachingConfig {
    public AccessType getAccessType();

    public boolean isMutable();

    public boolean isVersioned();

    public NavigableRole getNavigableRole();
}

