/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.spi.AbstractCollectionEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.collection.CollectionPersister;

public class PreCollectionRecreateEvent
extends AbstractCollectionEvent {
    public PreCollectionRecreateEvent(CollectionPersister collectionPersister, PersistentCollection collection, EventSource source) {
        super(collectionPersister, collection, source, collection.getOwner(), PreCollectionRecreateEvent.getOwnerIdOrNull(collection.getOwner(), source));
    }
}

