/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.CollectionAction;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public final class QueuedOperationCollectionAction
extends CollectionAction {
    public QueuedOperationCollectionAction(PersistentCollection collection, CollectionPersister persister, Serializable id, SharedSessionContractImplementor session) {
        super(persister, collection, id, session);
    }

    @Override
    public void execute() throws HibernateException {
        this.getPersister().processQueuedOps(this.getCollection(), this.getKey(), this.getSession());
        ((AbstractPersistentCollection)this.getCollection()).clearOperationQueue();
        CollectionEntry ce = this.getSession().getPersistenceContextInternal().getCollectionEntry(this.getCollection());
        if (!(ce.isDoremove() || ce.isDoupdate() || ce.isDorecreate())) {
            ce.afterAction(this.getCollection());
        }
    }
}

