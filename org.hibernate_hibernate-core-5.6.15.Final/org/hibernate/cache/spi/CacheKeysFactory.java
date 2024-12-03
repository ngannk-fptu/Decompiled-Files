/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public interface CacheKeysFactory {
    public Object createCollectionKey(Object var1, CollectionPersister var2, SessionFactoryImplementor var3, String var4);

    public Object createEntityKey(Object var1, EntityPersister var2, SessionFactoryImplementor var3, String var4);

    public Object createNaturalIdKey(Object[] var1, EntityPersister var2, SharedSessionContractImplementor var3);

    public Object getEntityId(Object var1);

    public Object getCollectionId(Object var1);

    public Object[] getNaturalIdValues(Object var1);
}

