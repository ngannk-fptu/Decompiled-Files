/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.spi;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.service.Service;

public interface PersisterFactory
extends Service {
    public EntityPersister createEntityPersister(PersistentClass var1, EntityDataAccess var2, NaturalIdDataAccess var3, PersisterCreationContext var4) throws HibernateException;

    public CollectionPersister createCollectionPersister(Collection var1, CollectionDataAccess var2, PersisterCreationContext var3) throws HibernateException;
}

