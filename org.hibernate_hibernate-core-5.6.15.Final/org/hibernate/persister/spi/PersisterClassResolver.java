/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.spi;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.Service;

public interface PersisterClassResolver
extends Service {
    public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass var1);

    public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection var1);
}

