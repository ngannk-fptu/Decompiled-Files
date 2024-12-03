/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import java.io.Serializable;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.query.NativeQuery;

public final class NamedQueryCollectionInitializer
implements CollectionInitializer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(NamedQueryCollectionInitializer.class);
    private final String queryName;
    private final CollectionPersister persister;

    public NamedQueryCollectionInitializer(String queryName, CollectionPersister persister) {
        this.queryName = queryName;
        this.persister = persister;
    }

    @Override
    public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
        LOG.debugf("Initializing collection: %s using named query: %s", this.persister.getRole(), this.queryName);
        NativeQuery nativeQuery = session.getNamedNativeQuery(this.queryName);
        if (nativeQuery.getParameterMetadata().hasNamedParameters()) {
            nativeQuery.setParameter(nativeQuery.getParameterMetadata().getNamedParameterNames().iterator().next(), (Object)key, this.persister.getKeyType());
        } else {
            nativeQuery.setParameter(1, (Object)key, this.persister.getKeyType());
        }
        nativeQuery.setCollectionKey(key).setFlushMode(FlushMode.MANUAL).list();
    }
}

