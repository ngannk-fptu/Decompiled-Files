/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.collection.CollectionPersister;

public abstract class AbstractCollectionEvent
extends AbstractEvent {
    private final PersistentCollection collection;
    private final Object affectedOwner;
    private final Serializable affectedOwnerId;
    private final String affectedOwnerEntityName;

    public AbstractCollectionEvent(CollectionPersister collectionPersister, PersistentCollection collection, EventSource source, Object affectedOwner, Serializable affectedOwnerId) {
        super(source);
        this.collection = collection;
        this.affectedOwner = affectedOwner;
        this.affectedOwnerId = affectedOwnerId;
        this.affectedOwnerEntityName = AbstractCollectionEvent.getAffectedOwnerEntityName(collectionPersister, affectedOwner, source);
    }

    protected static CollectionPersister getLoadedCollectionPersister(PersistentCollection collection, EventSource source) {
        CollectionEntry ce = source.getPersistenceContextInternal().getCollectionEntry(collection);
        return ce == null ? null : ce.getLoadedPersister();
    }

    protected static Object getLoadedOwnerOrNull(PersistentCollection collection, EventSource source) {
        return source.getPersistenceContextInternal().getLoadedCollectionOwnerOrNull(collection);
    }

    protected static Serializable getLoadedOwnerIdOrNull(PersistentCollection collection, EventSource source) {
        return source.getPersistenceContextInternal().getLoadedCollectionOwnerIdOrNull(collection);
    }

    protected static Serializable getOwnerIdOrNull(Object owner, EventSource source) {
        EntityEntry ownerEntry = source.getPersistenceContextInternal().getEntry(owner);
        return ownerEntry == null ? null : ownerEntry.getId();
    }

    protected static String getAffectedOwnerEntityName(CollectionPersister collectionPersister, Object affectedOwner, EventSource source) {
        EntityEntry ee;
        String entityName;
        String string = entityName = collectionPersister == null ? null : collectionPersister.getOwnerEntityPersister().getEntityName();
        if (affectedOwner != null && (ee = source.getPersistenceContextInternal().getEntry(affectedOwner)) != null && ee.getEntityName() != null) {
            entityName = ee.getEntityName();
        }
        return entityName;
    }

    public PersistentCollection getCollection() {
        return this.collection;
    }

    public Object getAffectedOwnerOrNull() {
        return this.affectedOwner;
    }

    public Serializable getAffectedOwnerIdOrNull() {
        return this.affectedOwnerId;
    }

    public String getAffectedOwnerEntityName() {
        return this.affectedOwnerEntityName;
    }
}

