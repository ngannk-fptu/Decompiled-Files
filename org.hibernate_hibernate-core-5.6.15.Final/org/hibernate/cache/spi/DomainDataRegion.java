/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.metamodel.model.domain.NavigableRole;

public interface DomainDataRegion
extends Region {
    public EntityDataAccess getEntityDataAccess(NavigableRole var1);

    public NaturalIdDataAccess getNaturalIdDataAccess(NavigableRole var1);

    public CollectionDataAccess getCollectionDataAccess(NavigableRole var1);
}

