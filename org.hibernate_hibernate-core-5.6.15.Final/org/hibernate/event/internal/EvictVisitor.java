/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.internal.AbstractVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.CollectionType;

public class EvictVisitor
extends AbstractVisitor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EvictVisitor.class);
    private Object owner;

    public EvictVisitor(EventSource session, Object owner) {
        super(session);
        this.owner = owner;
    }

    @Override
    Object processCollection(Object collection, CollectionType type) throws HibernateException {
        if (collection != null) {
            this.evictCollection(collection, type);
        }
        return null;
    }

    public void evictCollection(Object value, CollectionType type) {
        PersistentCollection collection;
        EventSource session = this.getSession();
        if (type.hasHolder()) {
            collection = session.getPersistenceContextInternal().removeCollectionHolder(value);
        } else if (value instanceof PersistentCollection) {
            collection = (PersistentCollection)value;
        } else if (value == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
            collection = (PersistentCollection)type.resolve(value, session, this.owner);
        } else {
            return;
        }
        if (collection != null && collection.unsetSession(session)) {
            this.evictCollection(collection);
        }
    }

    private void evictCollection(PersistentCollection collection) {
        PersistenceContext persistenceContext = this.getSession().getPersistenceContextInternal();
        CollectionEntry ce = persistenceContext.removeCollectionEntry(collection);
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Evicting collection: %s", MessageHelper.collectionInfoString(ce.getLoadedPersister(), collection, ce.getLoadedKey(), this.getSession()));
        }
        if (ce.getLoadedPersister() != null && ce.getLoadedPersister().getBatchSize() > 1) {
            persistenceContext.getBatchFetchQueue().removeBatchLoadableCollection(ce);
        }
        if (ce.getLoadedPersister() != null && ce.getLoadedKey() != null) {
            persistenceContext.removeCollectionByKey(new CollectionKey(ce.getLoadedPersister(), ce.getLoadedKey()));
        }
    }

    @Override
    boolean includeEntityProperty(Object[] values, int i) {
        return true;
    }
}

